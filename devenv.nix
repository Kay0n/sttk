# @autorun
{ pkgs, ... }: 

let
  libs = with pkgs; [
    libpulseaudio 
    libGL
    flite # needed for narrator
  ];
in
{

  env.LD_LIBRARY_PATH = "${pkgs.lib.makeLibraryPath libs}:$LD_LIBRARY_PATH";
  packages = with pkgs; [];

  scripts.run.exec = ''
    $DEVENV_ROOT/gradlew runClient
  '';

}