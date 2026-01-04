package main

import (
	// "AnixartGtk/internal" // config disabled
	"AnixartGtk/ui"
	"os"
	"path/filepath"
	"runtime"

	"github.com/adrg/xdg"
	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/gio/v2"
)

func pathExists(path string) bool {
	_, err := os.Stat(path)
	return err == nil
}

func addEnvPathList(key, value string) {
	if value == "" {
		return
	}
	if existing := os.Getenv(key); existing != "" {
		_ = os.Setenv(key, existing+string(os.PathListSeparator)+value)
		return
	}
	_ = os.Setenv(key, value)
}

func setEnvIfEmpty(key, value string) {
	if value == "" || os.Getenv(key) != "" {
		return
	}
	_ = os.Setenv(key, value)
}

func configureBundledEnv() {
	if runtime.GOOS != "windows" {
		return
	}
	exePath, err := os.Executable()
	if err != nil {
		return
	}
	exeDir := filepath.Dir(exePath)
	shareDir := filepath.Join(exeDir, "share")
	libDir := filepath.Join(exeDir, "lib")
	if pathExists(shareDir) {
		addEnvPathList("XDG_DATA_DIRS", shareDir)
	}
	schemaDir := filepath.Join(shareDir, "glib-2.0", "schemas")
	if pathExists(schemaDir) {
		setEnvIfEmpty("GSETTINGS_SCHEMA_DIR", schemaDir)
	}
	pixbufDir := filepath.Join(libDir, "gdk-pixbuf-2.0", "2.10.0", "loaders")
	if pathExists(pixbufDir) {
		setEnvIfEmpty("GDK_PIXBUF_MODULEDIR", pixbufDir)
	}
	pixbufCache := filepath.Join(libDir, "gdk-pixbuf-2.0", "2.10.0", "loaders.cache")
	if pathExists(pixbufCache) {
		setEnvIfEmpty("GDK_PIXBUF_MODULE_FILE", pixbufCache)
	}
}

func main() {
	// config, err := internal.ParseConfig()
	// if err != nil {
	// 	config = &internal.Config{}
	// }
	// internal.ConfigData = config
	configureBundledEnv()
	adw.Init()
	xdg.CacheFile("anixartgtk")
	// xdg.ConfigFile("anixartgtk/config.json")
	app := adw.NewApplication("com.github.imnottimaq.anixartgtk", gio.ApplicationFlagsNone)
	app.ConnectActivate(func() { ui.Activate(app) })

	if code := app.Run(os.Args); code > 0 {
		os.Exit(code)
	}
}
