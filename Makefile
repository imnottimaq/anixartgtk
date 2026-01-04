.NOTPARALLEL:

APP_NAME := anixartgtk
BPL_SOURCES := $(wildcard ui/templates/*.blp)
UI_TARGETS := $(BPL_SOURCES:.blp=.ui)
EXE_EXT :=
ifeq ($(OS),Windows_NT)
	EXE_EXT := .exe
endif
BIN := bin/$(APP_NAME)$(EXE_EXT)

GUI_LDFLAGS :=
MIN_LDFLAGS :=
ifeq ($(OS),Windows_NT)
	GUI_LDFLAGS := -ldflags "-H=windowsgui"
	MIN_LDFLAGS := -ldflags "-H=windowsgui -s -w"
endif


BUNDLE_DIR := bin
RUNTIME_PREFIX :=
ifeq ($(MSYSTEM),MINGW64)
	RUNTIME_PREFIX := /mingw64
else ifeq ($(MSYSTEM),UCRT64)
	RUNTIME_PREFIX := /ucrt64
endif


all: $(UI_TARGETS) build

run: all exec

test: all exec clean

ui/templates/%.ui: ui/templates/%.blp
	@echo "Compiling $< -> $@"
	@PYTHONUTF8=1 PYTHONIOENCODING=utf-8 blueprint-compiler compile --output $@ $<

build: $(UI_TARGETS)
	@echo "Building Go binary..."
	@rm -rf bin
	@mkdir -p bin
	@CGO_ENABLED=1 go build $(GUI_LDFLAGS) -o $(BIN) .
	@rm -f ui/templates/*.ui
	@echo "Build complete: $(BIN)"

build-min: $(UI_TARGETS)
	@echo "Building Go binary (min)..."
	@rm -rf bin
	@mkdir -p bin
	@CGO_ENABLED=1 go build $(MIN_LDFLAGS) -o $(BIN) .
	@rm -f ui/templates/*.ui
	@echo "Build complete: $(BIN)"

windows: build-min
	@if [ -z "$(RUNTIME_PREFIX)" ]; then echo "Error: MSYSTEM must be MINGW64 or UCRT64"; exit 1; fi
	@echo "Copying minimal DLLs into $(BUNDLE_DIR)..."
	@mkdir -p $(BUNDLE_DIR)
	@ldd $(BIN) | awk '$$3 ~ /\/(mingw64|ucrt64)\/bin\/.*\.dll/ {print $$3}' | while read -r dll; do \
		cp -f $$dll $(BUNDLE_DIR)/; \
	done
	@mkdir -p $(BUNDLE_DIR)/share $(BUNDLE_DIR)/lib $(BUNDLE_DIR)/share/icons
	@cp -r $(RUNTIME_PREFIX)/share/glib-2.0 $(BUNDLE_DIR)/share/ 2>/dev/null || true
	@cp -r $(RUNTIME_PREFIX)/share/gtk-4.0 $(BUNDLE_DIR)/share/ 2>/dev/null || true
	@cp -r $(RUNTIME_PREFIX)/share/libadwaita-1 $(BUNDLE_DIR)/share/ 2>/dev/null || true
	@cp -r $(RUNTIME_PREFIX)/lib/gdk-pixbuf-2.0 $(BUNDLE_DIR)/lib/ 2>/dev/null || true
	@cp -r $(RUNTIME_PREFIX)/share/icons/Adwaita $(BUNDLE_DIR)/share/icons/ 2>/dev/null || true
	@cp -r $(RUNTIME_PREFIX)/share/icons/hicolor $(BUNDLE_DIR)/share/icons/ 2>/dev/null || true
	@glib-compile-schemas $(BUNDLE_DIR)/share/glib-2.0/schemas 2>/dev/null || true
	@GDK_PIXBUF_MODULEDIR="$(BUNDLE_DIR)/lib/gdk-pixbuf-2.0/2.10.0/loaders" \
		gdk-pixbuf-query-loaders > "$(BUNDLE_DIR)/lib/gdk-pixbuf-2.0/2.10.0/loaders.cache" 2>/dev/null || true
	@rm -rf $(BUNDLE_DIR)/share/locale $(BUNDLE_DIR)/share/doc $(BUNDLE_DIR)/share/man 2>/dev/null || true
	@echo "Minimal DLL copy complete: $(BUNDLE_DIR)"


exec:
	@echo "Executing Go binary..."
	@test -f $(BIN) || (echo "Error: Binary not found at $(BIN)" && exit 1)
	@./$(BIN)

cleanup:
	@echo "Cleaning up generated .ui files..."
	@rm -f ui/templates/*.ui

clean: cleanup
	@echo "Cleaning up binary..."
	@rm -rf bin/

.PHONY: all run build build-min windows exec cleanup clean test
