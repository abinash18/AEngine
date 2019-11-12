package net.abi.abisEngine.rendering.meshLoading;

import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiImportFileEx;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.assimp.AIFile;
import org.lwjgl.assimp.AIFileCloseProc;
import org.lwjgl.assimp.AIFileCloseProcI;
import org.lwjgl.assimp.AIFileIO;
import org.lwjgl.assimp.AIFileOpenProc;
import org.lwjgl.assimp.AIFileOpenProcI;
import org.lwjgl.assimp.AIFileReadProc;
import org.lwjgl.assimp.AIFileReadProcI;
import org.lwjgl.assimp.AIFileSeek;
import org.lwjgl.assimp.AIFileSeekI;
import org.lwjgl.assimp.AIFileTellProc;
import org.lwjgl.assimp.AIFileTellProcI;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import net.abi.abisEngine.util.IOUtil;

public class AIMeshLoader {

	/* These Enums have been adopted from the original LWJGL AssImp class. */

	/** Default values for configuration properties. */
	public static final int AI_SBBC_DEFAULT_MAX_BONES = 0x3C, AI_SLM_DEFAULT_MAX_TRIANGLES = 0xF4240,
			AI_SLM_DEFAULT_MAX_VERTICES = 0xF4240, AI_LBW_MAX_WEIGHTS = 0x4, PP_ICL_PTCACHE_SIZE = 0xC,
			AI_IMPORT_IFC_DEFAULT_CYLINDRICAL_TESSELLATION = 0x20;

	/** Default value for configuration properties. */
	public static final float AI_DEBONE_THRESHOLD = 1.0f, AI_IMPORT_IFC_DEFAULT_SMOOTHING_ANGLE = 10.0f,
			AI_CONFIG_GLOBAL_SCALE_FACTOR_DEFAULT = 1.0f, AI_CONFIG_APP_SCALE_DEFAULT = 1.0f;

	/** UVCoord Transforms */
	public static final int AI_UVTRAFO_SCALING = 0x1, AI_UVTRAFO_ROTATION = 0x2, AI_UVTRAFO_TRANSLATION = 0x4,
			AI_UVTRAFO_ALL = (AI_UVTRAFO_SCALING | AI_UVTRAFO_ROTATION | AI_UVTRAFO_TRANSLATION);

	public static final int aiComponent_NORMALS = 0x2, aiComponent_TANGENTS_AND_BITANGENTS = 0x4,
			aiComponent_COLORS = 0x8, aiComponent_TEXCOORDS = 0x10, aiComponent_BONEWEIGHTS = 0x20,
			aiComponent_ANIMATIONS = 0x40, aiComponent_TEXTURES = 0x80, aiComponent_LIGHTS = 0x100,
			aiComponent_CAMERAS = 0x200, aiComponent_MESHES = 0x400, aiComponent_MATERIALS = 0x800;

	/** This is PI. Hi PI. */
	public static final double AI_MATH_PI = 3.141592653589793d, AI_MATH_TWO_PI = AI_MATH_PI * 2.0,
			AI_MATH_HALF_PI = AI_MATH_PI * 0.5;

	/** This is PI. Hi PI. */
	public static final float AI_MATH_PI_F = 3.1415927f, AI_MATH_TWO_PI_F = AI_MATH_PI_F * 2f,
			AI_MATH_HALF_PI_F = AI_MATH_PI_F * 0f;

	/** Numerical limits. */
	public static final float ai_epsilon = 0.00001f;

	/** Maximum dimension for strings, ASSIMP strings are zero terminated. */
	public static final int MAXLEN = 0x400;

	public static final int AI_TRUE = 0x1, AI_FALSE = 0x0;

	/**
	 * Defines the flags for all possible post processing steps.
	 */
	public static final int aiProcess_CalcTangentSpace = 0x1, aiProcess_JoinIdenticalVertices = 0x2,
			aiProcess_MakeLeftHanded = 0x4, aiProcess_Triangulate = 0x8, aiProcess_RemoveComponent = 0x10,
			aiProcess_GenNormals = 0x20, aiProcess_GenSmoothNormals = 0x40, aiProcess_SplitLargeMeshes = 0x80,
			aiProcess_PreTransformVertices = 0x100, aiProcess_LimitBoneWeights = 0x200,
			aiProcess_ValidateDataStructure = 0x400, aiProcess_ImproveCacheLocality = 0x800,
			aiProcess_RemoveRedundantMaterials = 0x1000, aiProcess_FixInfacingNormals = 0x2000,
			aiProcess_SortByPType = 0x8000, aiProcess_FindDegenerates = 0x10000, aiProcess_FindInvalidData = 0x20000,
			aiProcess_GenUVCoords = 0x40000, aiProcess_TransformUVCoords = 0x80000, aiProcess_FindInstances = 0x100000,
			aiProcess_OptimizeMeshes = 0x200000, aiProcess_OptimizeGraph = 0x400000, aiProcess_FlipUVs = 0x800000,
			aiProcess_FlipWindingOrder = 0x1000000, aiProcess_SplitByBoneCount = 0x2000000,
			aiProcess_Debone = 0x4000000, aiProcess_GlobalScale = 0x8000000, aiProcess_EmbedTextures = 0x10000000,
			aiProcess_ForceGenNormals = 0x20000000, aiProcess_DropNormals = 0x40000000,
			aiProcess_GenBoundingBoxes = 0x80000000;

	/**
	 * Process Presets
	 */
	public static final int aiProcess_ConvertToLeftHanded = aiProcess_MakeLeftHanded | aiProcess_FlipUVs
			| aiProcess_FlipWindingOrder | 0,
			aiProcessPreset_TargetRealtime_Fast = aiProcess_CalcTangentSpace | aiProcess_GenNormals
					| aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_GenUVCoords
					| aiProcess_SortByPType | 0,
			aiProcessPreset_TargetRealtime_Quality = aiProcess_CalcTangentSpace | aiProcess_GenSmoothNormals
					| aiProcess_JoinIdenticalVertices | aiProcess_ImproveCacheLocality | aiProcess_LimitBoneWeights
					| aiProcess_RemoveRedundantMaterials | aiProcess_SplitLargeMeshes | aiProcess_Triangulate
					| aiProcess_GenUVCoords | aiProcess_SortByPType | aiProcess_FindDegenerates
					| aiProcess_FindInvalidData | 0,
			aiProcessPreset_TargetRealtime_MaxQuality = aiProcessPreset_TargetRealtime_Quality | aiProcess_FindInstances
					| aiProcess_FindInvalidData | aiProcess_OptimizeMeshes | 0;
	/**
	 * Enumerates all supported types of light sources.
	 */
	public static final int aiLightSource_UNDEFINED = 0x0, aiLightSource_DIRECTIONAL = 0x1, aiLightSource_POINT = 0x2,
			aiLightSource_SPOT = 0x3, aiLightSource_AMBIENT = 0x4, aiLightSource_AREA = 0x5;

	/** Name for default materials (2nd is used if meshes have UV coords) */
	public static final String AI_DEFAULT_MATERIAL_NAME = "DefaultMaterial";

	/**
	 * Defines how the Nth texture of a specific type is combined with the result of
	 * all previous layers.
	 */
	public static final int aiTextureOp_Multiply = 0x0, aiTextureOp_Add = 0x1, aiTextureOp_Subtract = 0x2,
			aiTextureOp_Divide = 0x3, aiTextureOp_SmoothAdd = 0x4, aiTextureOp_SignedAdd = 0x5;

	/**
	 * Defines how UV coordinates outside the [0...1] range are handled. Commonly
	 * referred to as 'wrapping mode'.
	 */
	public static final int aiTextureMapMode_Wrap = 0x0, aiTextureMapMode_Clamp = 0x1, aiTextureMapMode_Decal = 0x3,
			aiTextureMapMode_Mirror = 0x2;

	/**
	 * Defines how the mapping coords for a texture are generated.
	 */
	public static final int aiTextureMapping_UV = 0x0, aiTextureMapping_SPHERE = 0x1, aiTextureMapping_CYLINDER = 0x2,
			aiTextureMapping_BOX = 0x3, aiTextureMapping_PLANE = 0x4, aiTextureMapping_OTHER = 0x5;

	/**
	 * Defines the purpose of a texture.
	 */
	public static final int aiTextureType_NONE = 0x0, aiTextureType_DIFFUSE = 0x1, aiTextureType_SPECULAR = 0x2,
			aiTextureType_AMBIENT = 0x3, aiTextureType_EMISSIVE = 0x4, aiTextureType_HEIGHT = 0x5,
			aiTextureType_NORMALS = 0x6, aiTextureType_SHININESS = 0x7, aiTextureType_OPACITY = 0x8,
			aiTextureType_DISPLACEMENT = 0x9, aiTextureType_LIGHTMAP = 0xA, aiTextureType_REFLECTION = 0xB,
			aiTextureType_UNKNOWN = 0xC;
	/**
	 * Defines all shading models supported by the library.
	 */
	public static final int aiShadingMode_Flat = 0x1, aiShadingMode_Gouraud = 0x2, aiShadingMode_Phong = 0x3,
			aiShadingMode_Blinn = 0x4, aiShadingMode_Toon = 0x5, aiShadingMode_OrenNayar = 0x6,
			aiShadingMode_Minnaert = 0x7, aiShadingMode_CookTorrance = 0x8, aiShadingMode_NoShading = 0x9,
			aiShadingMode_Fresnel = 0xA;

	/**
	 * Defines some mixed flags for a particular texture.
	 */
	public static final int aiTextureFlags_Invert = 0x1, aiTextureFlags_UseAlpha = 0x2,
			aiTextureFlags_IgnoreAlpha = 0x2;

	/**
	 * Defines alpha-blend flags.
	 */
	public static final int aiBlendMode_Default = 0x0, aiBlendMode_Additive = 0x1;

	/**
	 * A very primitive RTTI system for the contents of material properties.
	 */
	public static final int aiPTI_Float = 0x1, aiPTI_Double = 0x2, aiPTI_String = 0x3, aiPTI_Integer = 0x4,
			aiPTI_Buffer = 0x5;

	/** Material keys */
	public static final String AI_MATKEY_NAME = "?mat.name", AI_MATKEY_TWOSIDED = "$mat.twosided",
			AI_MATKEY_SHADING_MODEL = "$mat.shadingm", AI_MATKEY_ENABLE_WIREFRAME = "$mat.wireframe",
			AI_MATKEY_BLEND_FUNC = "$mat.blend", AI_MATKEY_OPACITY = "$mat.opacity",
			AI_MATKEY_TRANSPARENCYFACTOR = "$mat.transparencyfactor", AI_MATKEY_BUMPSCALING = "$mat.bumpscaling",
			AI_MATKEY_SHININESS = "$mat.shininess", AI_MATKEY_REFLECTIVITY = "$mat.reflectivity",
			AI_MATKEY_SHININESS_STRENGTH = "$mat.shinpercent", AI_MATKEY_REFRACTI = "$mat.refracti",
			AI_MATKEY_COLOR_DIFFUSE = "$clr.diffuse", AI_MATKEY_COLOR_AMBIENT = "$clr.ambient",
			AI_MATKEY_COLOR_SPECULAR = "$clr.specular", AI_MATKEY_COLOR_EMISSIVE = "$clr.emissive",
			AI_MATKEY_COLOR_TRANSPARENT = "$clr.transparent", AI_MATKEY_COLOR_REFLECTIVE = "$clr.reflective",
			AI_MATKEY_GLOBAL_BACKGROUND_IMAGE = "?bg.global", AI_MATKEY_GLOBAL_SHADERLANG = "?sh.lang",
			AI_MATKEY_SHADER_VERTEX = "?sh.vs", AI_MATKEY_SHADER_FRAGMENT = "?sh.fs", AI_MATKEY_SHADER_GEO = "?sh.gs",
			AI_MATKEY_SHADER_TESSELATION = "?sh.ts", AI_MATKEY_SHADER_PRIMITIVE = "?sh.ps",
			AI_MATKEY_SHADER_COMPUTE = "?sh.cs", _AI_MATKEY_TEXTURE_BASE = "$tex.file",
			_AI_MATKEY_UVWSRC_BASE = "$tex.uvwsrc", _AI_MATKEY_TEXOP_BASE = "$tex.op",
			_AI_MATKEY_MAPPING_BASE = "$tex.mapping", _AI_MATKEY_TEXBLEND_BASE = "$tex.blend",
			_AI_MATKEY_MAPPINGMODE_U_BASE = "$tex.mapmodeu", _AI_MATKEY_MAPPINGMODE_V_BASE = "$tex.mapmodev",
			_AI_MATKEY_TEXMAP_AXIS_BASE = "$tex.mapaxis", _AI_MATKEY_UVTRANSFORM_BASE = "$tex.uvtrafo",
			_AI_MATKEY_TEXFLAGS_BASE = "$tex.flags";

	/** PBR Material keys */
	public static final String aiAI_MATKEY_GLTF_PBRMETALLICROUGHNESS_BASE_COLOR_FACTOR = "$mat.gltf.pbrMetallicRoughness.baseColorFactor",
			aiAI_MATKEY_GLTF_PBRMETALLICROUGHNESS_METALLIC_FACTOR = "$mat.gltf.pbrMetallicRoughness.metallicFactor",
			aiAI_MATKEY_GLTF_PBRMETALLICROUGHNESS_ROUGHNESS_FACTOR = "$mat.gltf.pbrMetallicRoughness.roughnessFactor",
			aiAI_MATKEY_GLTF_ALPHAMODE = "$mat.gltf.alphaMode", aiAI_MATKEY_GLTF_ALPHACUTOFF = "$mat.gltf.alphaCutoff",
			aiAI_MATKEY_GLTF_PBRSPECULARGLOSSINESS = "$mat.gltf.pbrSpecularGlossiness",
			aiAI_MATKEY_GLTF_PBRSPECULARGLOSSINESS_GLOSSINESS_FACTOR = "$mat.gltf.pbrMetallicRoughness.glossinessFactor",
			aiAI_MATKEY_GLTF_UNLIT = "$mat.gltf.unlit", ai_AI_MATKEY_GLTF_TEXTURE_TEXCOORD_BASE = "$tex.file.texCoord",
			ai_AI_MATKEY_GLTF_MAPPINGNAME_BASE = "$tex.mappingname",
			ai_AI_MATKEY_GLTF_MAPPINGID_BASE = "$tex.mappingid",
			ai_AI_MATKEY_GLTF_MAPPINGFILTER_MAG_BASE = "$tex.mappingfiltermag",
			ai_AI_MATKEY_GLTF_MAPPINGFILTER_MIN_BASE = "$tex.mappingfiltermin",
			ai_AI_MATKEY_GLTF_SCALE_BASE = "$tex.scale", ai_AI_MATKEY_GLTF_STRENGTH_BASE = "$tex.strength";

	public static final int AI_MATKEY_GLTF_PBRMETALLICROUGHNESS_BASE_COLOR_TEXTURE = aiTextureType_DIFFUSE,
			AI_MATKEY_GLTF_PBRMETALLICROUGHNESS_METALLICROUGHNESS_TEXTURE = aiTextureType_UNKNOWN;

	/** Maximum number of indices per face (polygon). */
	public static final int AI_MAX_FACE_INDICES = 0x7FFF;

	/** Maximum number of indices per face (polygon). */
	public static final int AI_MAX_BONE_WEIGHTS = 0x7FFFFFFF;

	/** Maximum number of vertices per mesh. */
	public static final int AI_MAX_VERTICES = 0x7FFFFFFF;

	/** Maximum number of faces per mesh. */
	public static final int AI_MAX_FACES = 0x7FFFFFFF;

	/** Supported number of vertex color sets per mesh. */
	public static final int AI_MAX_NUMBER_OF_COLOR_SETS = 0x8;

	/** Supported number of texture coord sets (UV(W) channels) per mesh. */
	public static final int AI_MAX_NUMBER_OF_TEXTURECOORDS = 0x8;

	private static boolean readerInitialized = false;

	private static AIFileIO fileIO = null;

	/**
	 * Initializes the AI file reader.
	 */
	private static void initReader() {
		AIFileIO fileIo = AIFileIO.create();
		/* This just for preparing to read from the file. */
		AIFileOpenProcI fileOpenProc = new AIFileOpenProc() {
			public long invoke(long pFileIO, long fileName, long openMode) {
				AIFile aiFile = AIFile.create();
				final ByteBuffer data;
				String fileNameUtf8 = memUTF8(fileName);
				try {
					data = IOUtil.ioResourceToByteBuffer(fileNameUtf8, 8192);
				} catch (IOException e) {
					throw new RuntimeException("Could not open file: " + fileNameUtf8);
				}
				AIFileReadProcI fileReadProc = new AIFileReadProc() {
					public long invoke(long pFile, long pBuffer, long size, long count) {
						long max = Math.min(data.remaining(), size * count);
						memCopy(memAddress(data) + data.position(), pBuffer, max);
						return max;
					}
				};
				AIFileSeekI fileSeekProc = new AIFileSeek() {
					public int invoke(long pFile, long offset, int origin) {
						if (origin == Assimp.aiOrigin_CUR) {
							data.position(data.position() + (int) offset);
						} else if (origin == Assimp.aiOrigin_SET) {
							data.position((int) offset);
						} else if (origin == Assimp.aiOrigin_END) {
							data.position(data.limit() + (int) offset);
						}
						return 0;
					}
				};
				AIFileTellProcI fileTellProc = new AIFileTellProc() {
					public long invoke(long pFile) {
						return data.limit();
					}
				};
				aiFile.ReadProc(fileReadProc);
				aiFile.SeekProc(fileSeekProc);
				aiFile.FileSizeProc(fileTellProc);
				return aiFile.address();
			}
		};
		AIFileCloseProcI fileCloseProc = new AIFileCloseProc() {
			public void invoke(long pFileIO, long pFile) {
				/* Nothing to do */
			}
		};
		fileIo.set(fileOpenProc, fileCloseProc, NULL);
		readerInitialized = true;
	}

	/**
	 * Loads a mesh from a file using Asset Importer Library. NOTE:
	 * aiProcess_JoinIdenticalVertices is useful.
	 * 
	 * @return A Model Scene, since one model file can contain many individual
	 *         models.
	 */
	public static ModelScene loadModel(String modelFileName, int post_options) {

		if (!readerInitialized) {
			initReader();
		}
		// aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
		AIScene scene = aiImportFileEx(modelFileName, post_options, fileIO);
		if (scene == null) {
			throw new IllegalStateException(aiGetErrorString());
		}
		return new ModelScene(scene);
	}
}
