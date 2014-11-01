DESCRIPTION = "Hardware drivers & libs for Coolstream HD2"
SECTION = "base"
PRIORITY = "required"
LICENSE = "proprietary"
LIC_FILES_CHKSUM = "file://license;md5=17a6b3d5436a55985b200c725761907a"

COMPATIBLE_MACHINE = "coolstream-hd2"
# kernel modules are generally machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"
# inherit module

Pn = "r1"

PROVIDES += "virtual/stb-hal-libs"

KV = "2.6.34.14"
SRCREV = "${AUTOREV}"
PV = "0.0+git${SRCPV}"

SRC_URI = " \
	git://c00lstreamtech.de/cst-public-drivers.git \
	file://cs-drivers.init_${BOXTYPE} \
	file://mknodes \
	file://license \
"

S = "${WORKDIR}/git"

# The compiled binaries don't provide sonames.
SOLIBS = "${SOLIBSDEV}"

# These are proprietary binaries generated elsewhere so don't check ldflags
INSANE_SKIP_${PN} = "ldflags already-stripped"
INSANE_SKIP_${PN}-dev = "ldflags"

# no idea why do_configure does not find the license file otherwise...
do_configure_prepend () {
	cp '${WORKDIR}/license' '${S}'
}

do_compile () {
}

do_install () {
	install -d ${D}/lib/modules/${KV}
	cp -r ${S}/${BOXTYPE}/drivers/${KV}/extra ${D}/lib/modules/${KV}
	cp -r ${S}/${BOXTYPE}/drivers/${KV}/kernel ${D}/lib/modules/${KV}
	cp ${S}/${BOXTYPE}/drivers/${KV}/modules.* ${D}/lib/modules/${KV}
	# install -d ${D}${libdir}
	install -d ${D}/lib/firmware
	cp -r ${S}/${BOXTYPE}/libs/* ${D}/lib/
	cp -r ${S}/${BOXTYPE}/firmware/* ${D}/lib/firmware
	# init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/cs-drivers.init_${BOXTYPE} ${D}${sysconfdir}/init.d/cs-drivers
	install -m 0755 ${WORKDIR}/mknodes ${D}${sysconfdir}/init.d/mknodes
	update-rc.d -r ${D} mknodes start 60 S .
}

# initscript
inherit update-rc.d

INITSCRIPT_NAME = "cs-drivers"
INITSCRIPT_PARAMS = "start 50 S ."

FILES_${PN} = " \
	/lib/* \
	/lib/firmware/* \
	/lib/modules/* \
	${sysconfdir} \
"

# do not put the *.so into -dev package
FILES_${PN}-dev = ""