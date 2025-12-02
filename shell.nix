with import <nixpkgs> {};


let 
  libs = [
    libpulseaudio 
    libGL
    flite # needed for narrator to not throw errors
  ];
in
mkShell {
  buildInputs = libs;
  LD_LIBRARY_PATH = lib.makeLibraryPath libs;
}