{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = [
    pkgs.go
    pkgs.pkg-config
    pkgs.glib
    pkgs.gtk4
    pkgs.gobject-introspection
    pkgs.libadwaita
    pkgs.blueprint-compiler
    pkgs.mpv-unwrapped.dev
  ];

  # Это ключевой момент — чтобы pkg-config видел .pc-файлы
  PKG_CONFIG_PATH = with pkgs; lib.makeSearchPath "lib/pkgconfig" [
    glib
    gtk4
    gobject-introspection
    libadwaita
    mpv-unwrapped.dev
  ];

  shellHook = ''
    export CGO_ENABLED=1
    echo "Environment ready. You can now run 'make'"
  '';
}
