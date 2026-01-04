package ui

import (
	_ "embed"

	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/core/glib"
	"github.com/diamondburned/gotk4/pkg/gtk/v4"
)

//go:embed templates/window.ui
var windowXML string

func Activate(app *adw.Application) {
	builder := gtk.NewBuilderFromString(windowXML)
	window := builder.GetObject("AnixartGtk").Cast().(*adw.ApplicationWindow)
	window.SetApplication(&app.Application)

	navView := builder.GetObject("nav_view").Cast().(*adw.NavigationView)

	view := builder.GetObject("releases_view").Cast().(*gtk.ScrolledWindow)
	releasesBox := builder.GetObject("releases").Cast().(*gtk.Box)

	loadingBox, spinner := createLoadingBox("Загрузка релизов...")
	view.SetChild(loadingBox)

	window.Present()

	go func() {
		releaseTab := switchToReleasesTab(releasesBox, navView)
		glib.IdleAdd(func() {
			spinner.Stop()
			view.SetChild(releaseTab)
		})
	}()
}

func createLoadingBox(message string) (*gtk.Box, *gtk.Spinner) {
	loadingBox := gtk.NewBox(gtk.OrientationVertical, 12)
	loadingBox.SetVAlign(gtk.AlignCenter)
	loadingBox.SetHAlign(gtk.AlignCenter)

	spinner := gtk.NewSpinner()
	spinner.SetSizeRequest(48, 48)
	spinner.Start()

	loadingLabel := gtk.NewLabel(message)
	loadingLabel.AddCSSClass("title-2")

	loadingBox.Append(spinner)
	loadingBox.Append(loadingLabel)

	return loadingBox, spinner
}
