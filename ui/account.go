package ui

import (
	_ "embed"

	"github.com/diamondburned/gotk4-adwaita/pkg/adw"
	"github.com/diamondburned/gotk4/pkg/gtk/v3"
)

//go:embed templates/login_page.ui
var loginPageXML string

func switchToAccountTab(navView *adw.NavigationView) {
	loginPageBuilder := gtk.NewBuilderFromString(loginPageXML)
	loginPage := loginPageBuilder.GetObject("login-page").Cast().(*adw.NavigationView)
	navView.Push(loginPage)
}
