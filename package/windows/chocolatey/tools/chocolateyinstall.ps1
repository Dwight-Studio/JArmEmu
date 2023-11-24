$ErrorActionPreference = 'Stop'
$url        = 'https://github.com/Dwight-Studio/JArmEmu/releases/download/v0.1.6/JArmEmu-0.1.6-BETA.exe'

$packageArgs = @{
  packageName   = $env:ChocolateyPackageName
  fileType      = 'EXE'
  url           = $url

  softwareName  = 'JArmEmu*'

  checksum      = '13BC952808C11ED4F722D2E1A9A7F283FE850F373F90E718A5F0DF8B1C8B82CB'
  checksumType  = 'sha256'

  silentArgs   = '/VERYSILENT /SUPPRESSMSGBOXES /NORESTART /SP-'
  validExitCodes= @(0)
}

Install-ChocolateyPackage @packageArgs