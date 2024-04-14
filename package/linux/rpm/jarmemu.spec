Name:           jarmemu
Version:        0.2.0
Release:        3
Summary:        JArmEmu
Group:          Development/Tools
BuildArch:      noarch

License:        GPL-3.0
Packager:       Alexandre Leconte <aleconte@dwightstudio>
Source0:        %{name}-%{version}.tar.gz

Requires:       bash, desktop-file-utils

%description
The portable arm simulator
JArmEmu is a simple simulator with a graphical interface that offers basic control and information about a simulated ARMv7 architecture.

%prep
%setup -q

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/java
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/icons
mkdir -p $RPM_BUILD_ROOT/%{_bindir}

cp -r java/ $RPM_BUILD_ROOT/%{_datadir}/
cp -r icons/ $RPM_BUILD_ROOT/%{_datadir}/
cp -r mime/ $RPM_BUILD_ROOT/%{_datadir}/
cp -r metainfo/ $RPM_BUILD_ROOT/%{_datadir}/

install -Dm755 jarmemu $RPM_BUILD_ROOT/%{_bindir}/jarmemu

desktop-file-install --dir=%{buildroot}%{_datadir}/applications/ fr.dwightstudio.JArmEmu.desktop

%post
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ]; then
  %{_bindir}/gtk-update-icon-cache -q %{_datadir}/icons/hicolor;
fi
update-mime-database %{_datadir}/mime &> /dev/null || :
update-desktop-database &> /dev/null || :

%postun
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ]; then
  %{_bindir}/gtk-update-icon-cache -q %{_datadir}/icons/hicolor;
fi
update-mime-database %{_datadir}/mime &> /dev/null || :
update-desktop-database &> /dev/null || :

%clean
rm -rf $RPM_BUILD_ROOT

%files
%{_datadir}/java/JArmEmu/*

%{_datadir}/mime/packages/fr.dwightstudio.JArmEmu.xml
%{_datadir}/metainfo/fr.dwightstudio.JArmEmu.metainfo.xml

%{_bindir}/jarmemu

%{_datadir}/icons/hicolor/scalable/apps/fr.dwightstudio.JArmEmu.svg

%{_datadir}/applications/fr.dwightstudio.JArmEmu.desktop

%changelog
* Wed Nov 8 2023 Alexandre Leconte <aleconte@dwightstudio>
- Creating package
