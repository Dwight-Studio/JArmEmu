$ErrorActionPreference = 'Stop'
$url        = 'https://github.com/Dwight-Studio/JArmEmu/releases/download/v0.1.3/JArmEmu-0.1.3-BETA.exe'

$packageArgs = @{
  packageName   = $env:ChocolateyPackageName
  fileType      = 'EXE'
  url           = $url

  softwareName  = 'JArmEmu*

  checksum      = 'C1B65F358548C059E043ECD63CC96D8FFAFC10B616A7B0F5164A5A2D65533F77'
  checksumType  = 'sha256'

  silentArgs   = '/VERYSILENT /SUPPRESSMSGBOXES /NORESTART /SP-'
  validExitCodes= @(0)
}

Install-ChocolateyPackage @packageArgs