package ui

import (
	"AnixartGtk/internal"
	_ "embed"
	"fmt"

	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/gdk/v4"
	"github.com/diamondburned/gotk4/pkg/gdkpixbuf/v2"
	"github.com/diamondburned/gotk4/pkg/gtk/v4"
)

//go:embed templates/release_card.ui
var releaseCardXML string

//go:embed templates/releases.ui
var releasesXML string

//go:embed templates/release_page.ui
var releasePageXML string

func switchToReleasesTab(navView *adw.NavigationView) *gtk.Box {
	builder := gtk.NewBuilderFromString(releasesXML)
	tab := builder.GetObject("releases").Cast().(*gtk.Box)

	releases, err := internal.GetLatestReleases()
	if err != nil {
		errorLabel := gtk.NewLabel("Error while trying to parse info from Anixart.\nMore info on the console.")
		tab.Append(errorLabel)
		return tab
	}

	for _, release := range releases.Releases {
		releaseCard := newReleaseCard(release, navView)
		if releaseCard != nil {
			tab.Append(releaseCard)
		}
	}

	return tab
}

func newReleaseCard(release internal.Release, navView *adw.NavigationView) *gtk.Button {
	cardBuilder := gtk.NewBuilderFromString(releaseCardXML)
	releaseCard := cardBuilder.GetObject("release-card").Cast().(*gtk.Button)
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

	// Connect click handler
	releaseCard.ConnectClicked(func() {
		showReleaseDetail(release, navView)
	})

	return releaseCard
}

func showReleaseDetail(release internal.Release, navView *adw.NavigationView) {
	// Load the release page blueprint
	releasePageBuilder := gtk.NewBuilderFromString(releasePageXML)
	releasePage := releasePageBuilder.GetObject("release-detail").Cast().(*adw.NavigationPage)

	// Set the page title
	releasePage.SetTitle(release.Name)

	// Get all the widgets from the blueprint and populate them
	detailPoster := releasePageBuilder.GetObject("detail-poster").Cast().(*gtk.Picture)
	detailName := releasePageBuilder.GetObject("detail-name").Cast().(*gtk.Label)
	detailDescription := releasePageBuilder.GetObject("detail-description").Cast().(*gtk.Label)

	img, err := internal.GetPosterImage(release.PosterCacheName)
	if err == nil {
		pixbuf, err := gdkpixbuf.NewPixbufFromFile(img)
		if err == nil {
			originalWidth := pixbuf.Width()
			originalHeight := pixbuf.Height()
			targetWidth := 300
			targetHeight := int(float64(originalHeight) * float64(targetWidth) / float64(originalWidth))
			scaledPixbuf := pixbuf.ScaleSimple(targetWidth, targetHeight, gdkpixbuf.InterpBilinear)
			texture := gdk.NewTextureForPixbuf(scaledPixbuf)
			detailPoster.SetPaintable(texture)
		}
	}
	detailName.SetLabel(release.Name)
	detailDescription.SetLabel(release.Description)
	navView.Push(releasePage)
}
