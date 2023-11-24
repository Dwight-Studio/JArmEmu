$ErrorActionPreference = 'Stop'
$url        = 'https://github.com/Dwight-Studio/JArmEmu/releases/download/v0.1.6/JArmEmu-0.1.6-BETA.exe'

$packageArgs = @{
  packageName   = $env:ChocolateyPackageName
  fileType      = 'EXE'
  url           = $url

  softwareName  = 'JArmEmu*'

  checksum      = 'F3133227BAA348EF05BAA5A38440C69D1D1039C97B77A8B4343334BB6384E72D'
  checksumType  = 'sha256'

  silentArgs   = '/VERYSILENT /SUPPRESSMSGBOXES /NORESTART /SP-'
  validExitCodes= @(0)
}

Install-ChocolateyPackage @packageArgs