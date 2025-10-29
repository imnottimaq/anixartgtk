APP_NAME := anilibriagtk
BPL_SOURCES := $(wildcard ui/templates/*.blp)
UI_TARGETS := $(BPL_SOURCES:.blp=.ui)

all: $(UI_TARGETS) build

run: $(UI_TARGETS) build exec

ui/templates/%.ui: ui/templates/%.blp
	@echo "Compiling $< → $@"
	@blueprint-compiler compile --output $@ $<
build:
	@echo "Building Go binary..."
	@go build -o bin/anilibriagtk .
exec:
	@echo "Executing Go binary..."
	@bin/$(APP_NAME)
cleanup:
	@echo "Cleaning up generated .ui files..."
	rm -f ui/templates/*.ui

.PHONY: all run build exec cleanup clean