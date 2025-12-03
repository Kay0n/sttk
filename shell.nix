with import <nixpkgs> {};


let 
  libs = [
    libpulseaudio 
    libGL
    # jetbrains.jdk-no-jcef
    flite # needed for narrator to not throw errors
  ];
in
mkShell {
  buildInputs = libs;
  LD_LIBRARY_PATH = lib.makeLibraryPath libs;

  # shellHook = ''
  #   export JAVA_HOME=${jetbrains.jdk-no-jcef}
  #   export PATH=$JAVA_HOME/bin:$PATH
  # '';
}