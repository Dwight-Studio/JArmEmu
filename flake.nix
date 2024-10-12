{
  description = "Simple ARMv7 simulator written in Java, intended for educational purpose";

  inputs.nixpkgs.url = "nixpkgs";
  inputs.systems.url = "github:nix-systems/default";

  outputs =
    {
      self,
      nixpkgs,
      systems,
    }:
    let
      eachSystem = nixpkgs.lib.genAttrs (import systems);
      pkgsFor = eachSystem (system: import nixpkgs { localSystem = system; });
    in
    {
      packages = eachSystem (system: {
        default = self.packages.${system}.jarmemu;
        jarmemu = pkgsFor.${system}.callPackage (
          {
            lib,
            maven,
            jre,
            makeWrapper,
            wrapGAppsHook,
            extraJavaOpts ? [ ],
          }:
          let
            baseJavaOpts = [
              "-Dprism.dirtyopts=false"
              "--add-exports=javafx.base/com.sun.javafx.collections=ALL-UNNAMED"
              "--add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
            ];
          in
          maven.buildMavenPackage {
            pname = "jArmEmu";
            version = "1.1.0";

            src = self;

            mvnHash = "sha256-0AU6XTsTpalWlfWpOdcn6D+fDfC6GlliCoYXVqnOH9M=";

            nativeBuildInputs = [
              makeWrapper
              wrapGAppsHook
            ];

            installPhase = ''
              runHook preInstall

              cd jarmemu-distribution/target/jarmemu

              install -d $out/share/java/jarmemu/
              install -Dm644 *.jar $out/share/java/jarmemu/
              install -Dm644 lib/* -t $out/share/java/jarmemu/lib/

              makeWrapper ${jre}/bin/java $out/bin/jarmemu \
                --set _JAVA_OPTIONS "${toString (baseJavaOpts ++ extraJavaOpts)}" \
                --add-flags "-jar $out/share/java/jarmemu/jarmemu-launcher.jar"

              install -Dm644 resources/icons/hicolor/scalable/apps/fr.dwightstudio.JArmEmu.svg \
                $out/share/icons/hicolor/scalable/apps/fr.dwightstudio.JArmEmu.svg
              install -Dm 644 resources/mime/packages/fr.dwightstudio.JArmEmu.xml \
                $out/share/mime/packages/fr.dwightstudio.JArmEmu.xml
              install -Dm 644 resources/metainfo/fr.dwightstudio.JArmEmu.metainfo.xml \
                $out/share/metainfo/fr.dwightstudio.JArmEmu.metainfo.xml
              install -Dm644 resources/fr.dwightstudio.JArmEmu.desktop \
                $out/share/applications/fr.dwightstudio.JArmEmu.desktop

              runHook postInstall
            '';

            meta = with lib; {
              description = "Simple ARMv7 simulator written in Java, intended for educational purpose";
              homepage = "https://www.dwightstudio.fr/projects/JArmEmu";
              mainProgram = "jarmemu";
              license = licenses.gpl3Only;
            };
          }
        ) { jre = pkgsFor.${system}.jre.override { enableJavaFX = true; }; };
      });

      formatter = eachSystem (system: pkgsFor.${system}.nixfmt-rfc-style);
    };
}
