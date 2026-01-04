# anixartgtk

WIP Anixart app for PC

## Build prerequisites
- Go 1.24+
- `gcc` and `pkg-config`
- GTK4 + libadwaita development libraries
- Blueprint compiler (`blueprint-compiler`)

## Linux build (Ubuntu as an example)
Install dependencies:
- Ubuntu: `sudo apt install build-essential gcc pkg-config libgtk-4-dev libadwaita-1-dev blueprint-compiler`
- Fedora: `sudo dnf install gcc pkgconf-pkg-config gtk4-devel libadwaita-devel blueprint-compiler`
- Arch: `sudo pacman -S --needed base-devel pkgconf gtk4 libadwaita blueprint-compiler`

Build:
- `make`

## Windows build
Use either the `MINGW64` or `UCRT64` shell and match packages to the shell:
- `MINGW64`: `pacman -S --needed mingw-w64-x86_64-go mingw-w64-x86_64-gcc mingw-w64-x86_64-gtk4 mingw-w64-x86_64-libadwaita mingw-w64-x86_64-gobject-introspection mingw-w64-x86_64-blueprint-compiler mingw-w64-x86_64-pkg-config base-devel`
- `UCRT64`: `pacman -S --needed mingw-w64-ucrt-x86_64-go mingw-w64-ucrt-x86_64-gcc mingw-w64-ucrt-x86_64-gtk4 mingw-w64-ucrt-x86_64-libadwaita mingw-w64-ucrt-x86_64-gobject-introspection mingw-w64-ucrt-x86_64-blueprint-compiler mingw-w64-ucrt-x86_64-pkg-config base-devel`

Build:
- `make`

Optional (MINGW64):
- `build_windows.bat` (uses `C:\msys64` and runs `make windows`, then launches `bin\anixartgtk.exe`)

If `blueprint-compiler` is not found, double-check you are in the MinGW/UCRT shell (not `MSYS`) and that `/mingw64/bin` or `/ucrt64/bin` is on PATH.
