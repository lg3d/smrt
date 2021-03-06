#
# Shared Makefile rules for all documents. See the template
# for info on variables this will look for.
#

all: $(DOCNAME).pdf

CONVERTED_SVG_FIGURES = $(SVG_FIGURES:.svg=.pdf)
CONVERTED_PNG_FIGURES = $(PNG_FIGURES:.png=.eps)
SVG_TEMP_POSTSCRIPT = $(SVG_FIGURES:.svg=.ps)

ALL_FIGURES = \
	$(CONVERTED_SVG_FIGURES)	\
	$(CONVERTED_PNG_FIGURES)	\
	$(STATIC_FIGURES)		\
	$(GENERATED_FIGURES)

# Extra search paths for latex
TEXINPUTS = .:$(LIB_DIR)

# Note that we run pdflatex twice, so cross-references
# and the table of contents are up to date.
$(DOCNAME).pdf: *.tex $(ALL_FIGURES) $(LIB_DIR)/*.tex
	TEXINPUTS=$(TEXINPUTS):$$TEXINPUTS pdflatex $(DOCNAME).tex
	TEXINPUTS=$(TEXINPUTS):$$TEXINPUTS pdflatex $(DOCNAME).tex

presentation: *.tex $(ALL_FIGURES) $(LIB_DIR)/*.tex
	TEXINPUTS=$(TEXINPUTS):$$TEXINPUTS latex $(DOCNAME).tex
	TEXINPUTS=$(TEXINPUTS):$$TEXINPUTS latex $(DOCNAME).tex
	dvips -R0 $(DOCNAME).dvi -o $(DOCNAME).ps
	ps2pdf $(DOCNAME).ps $(DOCNAME).pdf

%.pdf: %.ps
	epstopdf $<

%.eps: %.png
	convert $< $@

%.ps: %.svg
	inkscape -z -f $< -p ">$@"

ALL_TEMPFILES = \
	*.ps *.bm *.log *.toc *.lof *.aux *.dvi *.glo *.out \
	$(CONVERTED_SVG_FIGURES)	\
	$(CONVERTED_PNG_FIGURES)   \
	$(GENERATED_FIGURES)

spellcheck: $(DOCNAME).tex
	aspell -t check $<

# Clean everything except the final output file
clean-tempfiles:
	rm -rf $(ALL_TEMPFILES)

clean: clean-tempfiles
	rm -rf $(DOCUMENTS)

