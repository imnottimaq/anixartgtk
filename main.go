package main

import (
	"AnixartGtk/internal"
	"AnixartGtk/ui"
	"os"
	"runtime"

	"github.com/adrg/xdg"
	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/gio/v2"
)

func main() {
	if runtime.GOOS == "windows" {
		internal.AttachConsoleIfPresent()
	}
	// config, err := internal.ParseConfig()
	// if err != nil {
	// 	config = &internal.Config{}
	// }
	// internal.ConfigData = config
	internal.ConfigureBundledEnv()
	adw.Init()
	xdg.CacheFile("anixartgtk")
	// xdg.ConfigFile("anixartgtk/config.json")
	app := adw.NewApplication("com.github.imnottimaq.anixartgtk", gio.ApplicationFlagsNone)
	app.ConnectActivate(func() { ui.Activate(app) })

	if code := app.Run(os.Args); code > 0 {
		os.Exit(code)
	}
}
