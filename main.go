package main

import (
	"AnixartGtk/internal"
	"AnixartGtk/ui"
	"os"

	"github.com/adrg/xdg"
	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/gio/v2"
)

func main() {
	internal.ConfigData, _ = internal.ParseConfig()
	adw.Init()
	xdg.CacheFile("anixartgtk")
	xdg.ConfigFile("anixartgtk/config.json")
	app := adw.NewApplication("com.github.imnottimaq.anixartgtk", gio.ApplicationFlagsNone)
	app.ConnectActivate(func() { ui.Activate(app) })

	if code := app.Run(os.Args); code > 0 {
		os.Exit(code)
	}
}
