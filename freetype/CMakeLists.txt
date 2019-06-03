cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/jni/include)
include_directories(src/main/jni/include/freetype)
include_directories(src/main/jni/include/freetype/config)
include_directories(src/main/jni/include/freetype/internal)
include_directories(src/main/jni/include/freetype/internal/services)

add_library( freetype
             SHARED
             src/main/jni/src/base/ftbbox.c
             src/main/jni/src/base/ftbitmap.c
             src/main/jni/src/base/ftfstype.c
             src/main/jni/src/base/ftglyph.c
             src/main/jni/src/base/ftlcdfil.c
             src/main/jni/src/base/ftstroke.c
             src/main/jni/src/base/fttype1.c
             src/main/jni/src/base/ftxf86.c
             src/main/jni/src/base/ftbase.c
             src/main/jni/src/base/ftsystem.c
             src/main/jni/src/base/ftinit.c
             src/main/jni/src/base/ftgasp.c
             src/main/jni/src/raster/raster.c
             src/main/jni/src/sfnt/sfnt.c
             src/main/jni/src/smooth/smooth.c
             src/main/jni/src/autofit/autofit.c
             src/main/jni/src/truetype/truetype.c
             src/main/jni/src/cff/cff.c
             src/main/jni/src/psnames/psnames.c
             src/main/jni/src/pshinter/pshinter.c
             src/main/jni/src/native-freetype.c )

add_definitions("-W -Wall")
add_definitions("-fPIC -DPIC")
add_definitions("-DDARWIN_NO_CARBON")
add_definitions("-DFT2_BUILD_LIBRARY")
add_definitions("-O2")


add_definitions("-W -Wall")
add_definitions("-fPIC -DPIC")
add_definitions("-DDARWIN_NO_CARBON")
add_definitions("-DFT2_BUILD_LIBRARY")
add_definitions("-O2")


find_library( log-lib
              log )

find_library( dl-lib dl)


target_link_libraries(freetype
                        ${log-lib}
                        ${dl-lib})
