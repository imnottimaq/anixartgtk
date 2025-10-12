package ui

import (
	"AnilibriaGtk/internal"
	_ "embed"
	"fmt"

	"github.com/diamondburned/gotk4/pkg/gtk/v4"
)

//go:embed templates/window.ui
var windowXML string

//go:embed templates/release_card.ui
var releaseCardXML string

//go:embed templates/releases.ui
var releasesXML string

func Activate(app *gtk.Application) {
	builder := gtk.NewBuilderFromString(windowXML)
	window := builder.GetObject("AnilibriaGtk").Cast().(*gtk.ApplicationWindow)
	main := builder.GetObject("main").Cast().(*gtk.ScrolledWindow)
	releaseTab := SwitchToReleasesTab()
	main.SetChild(releaseTab)
	window.SetApplication(app)
	window.Present()
}

func SwitchToReleasesTab() *gtk.Box {
	builder := gtk.NewBuilderFromString(releasesXML)
	tab := builder.GetObject("releases").Cast().(*gtk.Box)
	releases, err := internal.GetLatestReleases()
	if err != nil {
		fmt.Printf(fmt.Sprintf("%v", err))
		errorLabel := gtk.NewLabel("Error while trying to parse info from Anilibria.\nMore info on the console.")
		tab.Append(errorLabel)
	}
	for _, release := range releases {
		releaseCard := newReleaseCard(release)
		tab.Append(releaseCard)
	}
	return tab
}

func newReleaseCard(release internal.Release) *gtk.Box {
	cardBuilder := gtk.NewBuilderFromString(releaseCardXML)
	releaseCard := cardBuilder.GetObject("release-card").Cast().(*gtk.Box)
	picture := cardBuilder.GetObject("poster").Cast().(*gtk.Image)
	name := cardBuilder.GetObject("name").Cast().(*gtk.Label)
	description := cardBuilder.GetObject("description").Cast().(*gtk.Label)
	img, err := internal.GetPosterImage(release.Poster.Main)
	if err != nil {
		print(err)
		return nil
	}
	print(img)
	picture.SetFromFile(img)
	name.SetLabel(release.Name.Eng)
	description.SetLabel(release.Description)

	return releaseCard
}
