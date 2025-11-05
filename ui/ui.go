package ui

import (
	"AnilibriaGtk/internal"
	_ "embed"
	"fmt"

	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/core/glib"
	"github.com/diamondburned/gotk4/pkg/gdk/v4"
	"github.com/diamondburned/gotk4/pkg/gdkpixbuf/v2"
	"github.com/diamondburned/gotk4/pkg/gtk/v4"
)

//go:embed templates/window.ui
var windowXML string

//go:embed templates/release_card.ui
var releaseCardXML string

//go:embed templates/releases.ui
var releasesXML string

func Activate(app *adw.Application) {
	builder := gtk.NewBuilderFromString(windowXML)
	window := builder.GetObject("AnixartGtk").Cast().(*adw.ApplicationWindow)
	window.SetApplication(&app.Application)
	view := builder.GetObject("releases_view").Cast().(*gtk.ScrolledWindow)
	loadingBox, spinner := createLoadingBox("Загрузка релизов...")
	view.SetChild(loadingBox)
	window.Present()

	go func() {
		releaseTab := switchToReleasesTab()
		glib.IdleAdd(func() {
			spinner.Stop()
			view.SetChild(releaseTab)
		})
	}()
}

func switchToReleasesTab() *gtk.Box {
	builder := gtk.NewBuilderFromString(releasesXML)
	tab := builder.GetObject("releases").Cast().(*gtk.Box)
	releases, err := internal.GetLatestReleases()
	if err != nil {
		errorLabel := gtk.NewLabel("Error while trying to parse info from Anixart.\nMore info on the console.")
		tab.Append(errorLabel)
	}
	for _, release := range releases.Releases {
		releaseCard := newReleaseCard(release)
		tab.Append(releaseCard)
	}
	return tab
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

func newReleaseCard(release internal.Release) *gtk.Box {
	cardBuilder := gtk.NewBuilderFromString(releaseCardXML)
	releaseCard := cardBuilder.GetObject("release-card").Cast().(*gtk.Box)
	picture := cardBuilder.GetObject("poster").Cast().(*gtk.Picture)
	name := cardBuilder.GetObject("name").Cast().(*gtk.Label)
	description := cardBuilder.GetObject("description").Cast().(*gtk.Label)

	img, err := internal.GetPosterImage(release.PosterCacheName)
	if err != nil {
		fmt.Printf("Error getting poster: %v\n", err)
		return nil
	}

	pixbuf, err := gdkpixbuf.NewPixbufFromFile(img)
	if err != nil {
		fmt.Printf("Error loading pixbuf: %v\n", err)
		return nil
	}

	originalWidth := pixbuf.Width()
	originalHeight := pixbuf.Height()

	targetWidth := 120
	targetHeight := int(float64(originalHeight) * float64(targetWidth) / float64(originalWidth))

	scaledPixbuf := pixbuf.ScaleSimple(targetWidth, targetHeight, gdkpixbuf.InterpBilinear)

	texture := gdk.NewTextureForPixbuf(scaledPixbuf)
	picture.SetPaintable(texture)
	picture.SetSizeRequest(targetWidth, targetHeight)

	name.SetLabel(release.Name)
	description.SetLabel(release.Description)

	return releaseCard
}
