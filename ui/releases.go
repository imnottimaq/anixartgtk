package ui

import (
	"AnixartGtk/internal"
	_ "embed"
	"fmt"
	"strconv"

	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/gdk/v4"
	"github.com/diamondburned/gotk4/pkg/gdkpixbuf/v2"
	"github.com/diamondburned/gotk4/pkg/gtk/v4"
)

//go:embed templates/release_card.ui
var releaseCardXML string

//go:embed templates/release_page.ui
var releasePageXML string

//go:embed templates/select_dub_page.ui
var selectDubPageXML string

//go:embed templates/dub_card.ui
var dubCardXML string

func switchToReleasesTab(navView *adw.NavigationView) *gtk.Box {
	builder := gtk.NewBuilderFromString(windowXML)
	tab := builder.GetObject("releases").Cast().(*gtk.Box)
	tab.SetVisible(true)
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

	releaseCard.ConnectClicked(func() {
		showReleaseDetail(strconv.Itoa(release.Id), navView)
	})

	return releaseCard
}

func showReleaseDetail(id string, navView *adw.NavigationView) {
	data, err := internal.GetDetailedReleaseInfo(id)
	releaseDetails := data.Release
	if err != nil {
		print(fmt.Printf("%v", err))
	}
	print(fmt.Printf("%v", releaseDetails))

	releasePageBuilder := gtk.NewBuilderFromString(releasePageXML)
	releasePage := releasePageBuilder.GetObject("release-detail").Cast().(*adw.NavigationPage)

	detailPoster := releasePageBuilder.GetObject("detail-poster").Cast().(*gtk.Picture)
	detailName := releasePageBuilder.GetObject("detail-name").Cast().(*gtk.Label)
	detailNameAlternative := releasePageBuilder.GetObject("detail-name-alternative").Cast().(*gtk.Label)
	detailDescription := releasePageBuilder.GetObject("detail-description").Cast().(*gtk.Label)
	detailEpisodes := releasePageBuilder.GetObject("detail-episodes").Cast().(*gtk.Label)

	img, err := internal.GetPosterImage(releaseDetails.PosterCacheName)
	if err == nil {
		pixbuf, err := gdkpixbuf.NewPixbufFromFile(img)
		if err == nil {
			originalWidth := pixbuf.Width()
			originalHeight := pixbuf.Height()
			targetWidth := 200
			targetHeight := int(float64(originalHeight) * float64(targetWidth) / float64(originalWidth))
			scaledPixbuf := pixbuf.ScaleSimple(targetWidth, targetHeight, gdkpixbuf.InterpBilinear)
			texture := gdk.NewTextureForPixbuf(scaledPixbuf)
			detailPoster.SetPaintable(texture)
		}
	}
	detailName.SetLabel(releaseDetails.Name)
	detailEpisodes.SetLabel(strconv.Itoa(releaseDetails.EpisodesTotal))
	detailNameAlternative.SetLabel(releaseDetails.NameOriginal)
	detailDescription.SetLabel(releaseDetails.Description)
	watchButton := releasePageBuilder.GetObject("watch-button").Cast().(*gtk.Button)
	watchButton.ConnectClicked(func() {
		showSelectDub(id, navView)
	})
	navView.Push(releasePage)
}

func showSelectDub(id string, navView *adw.NavigationView) {
	if id == "" {
		return
	}
	providers, err := internal.GetDubProvidersForEpisode(id)
	if err != nil {
		return
	}
	selectDubPageBuilder := gtk.NewBuilderFromString(selectDubPageXML)
	selectDubNavPage := selectDubPageBuilder.GetObject("dub-selection").Cast().(*adw.NavigationPage)
	selectDubBox := selectDubPageBuilder.GetObject("dub-main").Cast().(*gtk.Box)
	for _, provider := range providers.Providers {
		dubCard := newDubCard(provider)
		selectDubBox.Append(dubCard)
	}
	navView.Push(selectDubNavPage)
}

func newDubCard(provider internal.DubProvider) *gtk.Box {
	builder := gtk.NewBuilderFromString(dubCardXML)
	dubTemplate := builder.GetObject("dub-card").Cast().(*gtk.Box)
	castLabel := builder.GetObject("cast").Cast().(*gtk.Label)
	if provider.Cast != "" {
		castLabel.SetLabel(provider.Cast)
		castLabel.SetVisible(true)
	} else {
		castLabel.SetVisible(false)
	}
	dubProviderName := builder.GetObject("provider").Cast().(*gtk.Label)
	dubProviderName.SetLabel(provider.Name)
	episodesCountLabel := builder.GetObject("episodes-count").Cast().(*gtk.Label)
	episodesCountLabel.SetLabel(strconv.Itoa(provider.EpisodesCount) + " серий")
	viewsCountLabel := builder.GetObject("views-count").Cast().(*gtk.Label)
	viewsCountLabel.SetLabel(strconv.Itoa(provider.ViewCount) + " просмотров")
	return dubTemplate
}
