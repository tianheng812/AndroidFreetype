#include <jni.h>
//#include "include/freetype/tttags.h"
//#include FT_FREETYPE_H
#include <android/log.h>
#include <freetype/tttags.h>

#define LOG_TAG "FontDecode"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static FT_Library library;
static FT_Face face;
static int error;

typedef jint i;

JNIEXPORT jboolean JNICALL
Java_com_xian_freetype_word_NdkFreeType_FT_1Init_1FreeType(JNIEnv *env, jclass instance,
                                                            jstring jFontPath_) {

    const char *fontPath = (*env)->GetStringUTFChars(env, jFontPath_, 0);
    LOGI("%s", fontPath);
    error = FT_Init_FreeType(&library);
    if (error) {
        LOGI("can not init free type library!\n");
        return JNI_FALSE;
    }

    error = FT_New_Face(library, fontPath, 0, &face);
    if (error) {
        LOGI("create new face falied!\n");
        return JNI_TRUE;
    }

    (*env)->ReleaseStringUTFChars(env, jFontPath_, fontPath);
    return JNI_TRUE;
}


JNIEXPORT jobject
Java_com_xian_freetype_word_NdkFreeType_FT_1GET_1Word_1Info(JNIEnv *env, jclass instance,
                                                             jint font_size,
                                                             jlong char_index) {
    if (face == NULL) {
        LOGI("create new face falied!\n");
        return NULL;
    }

    error = FT_Set_Pixel_Sizes(face, font_size, 0);
    if (error) {
        LOGI("set font size error!\n");
        return NULL;
    }

    FT_UInt uiGlyphIndex = FT_Get_Char_Index(face, char_index);

    error = FT_Load_Glyph(face, uiGlyphIndex, FT_LOAD_DEFAULT);

    if (error) {
        LOGI("Load char error!\n");
        return NULL;
    }

    if (face->glyph->format != FT_GLYPH_FORMAT_BITMAP) {
        error = FT_Render_Glyph(face->glyph, FT_RENDER_MODE_MONO);
        if (error) {
            LOGI("render char failed!\n");
            return NULL;
        }
    }

    FT_Bitmap bitmap = face->glyph->bitmap;

    LOGI("width=%d ", bitmap.width);
    LOGI("rows=%d ", bitmap.rows);
    LOGI("pitch=%d ", bitmap.pitch);
    LOGI("bitmap_left=%d ", face->glyph->bitmap_left);
    LOGI("bitmap_top=%d ", face->glyph->bitmap_top);
    LOGI("ascender=%d ", face->size->face->ascender >> 6);

    jclass clazz = (*env)->FindClass(env, "com/xian/freetype/word/WordInfo");
    jmethodID init = (*env)->GetMethodID(env, clazz, "<init>", "()V");
    jobject obj = (*env)->NewObject(env, clazz, init);

    error = FT_Set_Pixel_Sizes(face, font_size, 0);
    jfieldID height = (*env)->GetFieldID(env, clazz, "rows", "I");
    (*env)->SetIntField(env, obj, height, bitmap.rows);

    jfieldID width = (*env)->GetFieldID(env, clazz, "width", "I");
    (*env)->SetIntField(env, obj, width, bitmap.width);

    jfieldID left = (*env)->GetFieldID(env, clazz, "bitmap_left", "I");
    (*env)->SetIntField(env, obj, left, face->glyph->bitmap_left);

    jfieldID pitch = (*env)->GetFieldID(env, clazz, "pitch", "I");
    (*env)->SetIntField(env, obj, pitch, bitmap.pitch);

    jfieldID top = (*env)->GetFieldID(env, clazz, "bitmap_top", "I");
    (*env)->SetIntField(env, obj, top, face->glyph->bitmap_top);

    jbyteArray data = (*env)->NewByteArray(env, font_size * font_size);
    jfieldID bufferId = (*env)->GetFieldID(env, clazz, "buffer", "[B");
    (*env)->SetByteArrayRegion(env, data, 0, font_size * font_size, bitmap.buffer);
    (*env)->SetObjectField(env, obj, bufferId, data);

    /*char s[2048] = "";
    int i, j, k, counter;
    unsigned char temp;
    for (j = 0; j < (font_size * 26) / 32 - face->glyph->bitmap_top; j++) {
        for (i = 0; i < font_size; i++) {
            strcat(s, "-");

        }
        char *string1 = (char *) malloc(sizeof(char) * 16);
        sprintf(string1, "%02d\n", j);
        strcat(s, string1);
    }

    for (; j < face->glyph->bitmap.rows + (font_size * 26) / 32 - face->glyph->bitmap_top; j++) {
        for (i = 1; i <= face->glyph->bitmap_left; i++) {
            strcat(s, "-");
        }

        for (k = 0; k < face->glyph->bitmap.pitch; k++) {
            temp = face->glyph->bitmap.buffer[face->glyph->bitmap.pitch *
                                              (j + face->glyph->bitmap_top -
                                               (font_size * 26) / 32) + k];
            for (counter = 0; counter < 8; counter++) {
                if (temp & 0x80) {
                    strcat(s, "o");
                } else {
                    strcat(s, "-");
                }
                temp <<= 1;
                i++;
                if (i > font_size) {
                    break;
                }
            }
        }

        char *string1 = (char *) malloc(sizeof(char) * 16);
        sprintf(string1, "%02d\n", j);
        strcat(s, string1);
    }

    for (; j < font_size; j++) {
        for (i = 0; i < font_size; i++) {
            strcat(s, "-");
        }
        char *string1 = (char *) malloc(sizeof(char) * 16);
        sprintf(string1, "%02d\n", j);
        strcat(s, string1);
    }
    LOGI("%s\n", s);*/
    return obj;
}

JNIEXPORT void JNICALL
Java_com_xian_freetype_word_NdkFreeType_FT_1Destroy_1FreeType(JNIEnv *env, jclass type) {

    if (face != NULL)
        FT_Done_Face(face);
    if (library != NULL)
        FT_Done_FreeType(library);
}