.NOTPARALLEL:

APP_NAME := anixartgtk
BPL_SOURCES := $(wildcard ui/templates/*.blp)
UI_TARGETS := $(BPL_SOURCES:.blp=.ui)

all: $(UI_TARGETS) build

run: all exec

test: all exec clean

ui/templates/%.ui: ui/templates/%.blp
	@echo "Compiling $< → $@"
	@blueprint-compiler compile --output $@ $<

build: $(UI_TARGETS)
	@echo "Building Go binary..."
	@mkdir -p bin
	@go build -o bin/$(APP_NAME) .
	@echo "Build complete: bin/$(APP_NAME)"

exec:
	@echo "Executing Go binary..."
	@test -f bin/$(APP_NAME) || (echo "Error: Binary not found at bin/$(APP_NAME)" && exit 1)
	@./bin/$(APP_NAME)

cleanup:
	@echo "Cleaning up generated .ui files..."
	@rm -f ui/templates/*.ui

clean: cleanup
	@echo "Cleaning up binary..."
	@rm -rf bin/

.PHONY: all run build exec cleanup clean test