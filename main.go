package main

import (
	"AnilibriaGtk/ui"
	"os"

	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/gio/v2"
	"github.com/diamondburned/gotk4/pkg/gtk/v4"
)

func main() {
	adw.Init()
	app := gtk.NewApplication("com.github.imnottimaq.anilibriagtk", gio.ApplicationFlagsNone)
	app.ConnectActivate(func() { ui.Activate(app) })

	if code := app.Run(os.Args); code > 0 {
		os.Exit(code)
	}
}
