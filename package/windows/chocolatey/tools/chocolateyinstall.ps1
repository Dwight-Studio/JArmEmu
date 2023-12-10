$ErrorActionPreference = 'Stop'
$url        = 'https://github.com/Dwight-Studio/JArmEmu/releases/download/v0.1.7/JArmEmu-0.1.7-BETA.exe'

$packageArgs = @{
  packageName   = $env:ChocolateyPackageName
  fileType      = 'EXE'
  url           = $url

  softwareName  = 'JArmEmu*'

  checksum      = '4A2947A1E5F40808EEA78914E6394A151D76CE62665B2AC6E49E4F1512C270CB'
  checksumType  = 'sha256'

  silentArgs   = '/VERYSILENT /SUPPRESSMSGBOXES /NORESTART /SP-'
  validExitCodes= @(0)
}

Install-ChocolateyPackage @packageArgs