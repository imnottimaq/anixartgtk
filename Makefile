APP_NAME := anilibriagtk
BPL_SOURCES := $(wildcard ui/templates/*.blp)
UI_TARGETS := $(BPL_SOURCES:.blp=.ui)

all: $(UI_TARGETS) build cleanup

run: $(UI_TARGETS) build cleanup exec

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
	rm -f ui/templates/*.ui