package net.abi.abisEngine.rendering.meshLoading;

import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiImportFileEx;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
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
	public Model loadModel() {
		AIFileIO fileIo = AIFileIO.create();
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
		AIScene scene = aiImportFileEx("org/lwjgl/demo/opengl/assimp/magnet.obj",
				aiProcess_JoinIdenticalVertices | aiProcess_Triangulate, fileIo);
		if (scene == null) {
			throw new IllegalStateException(aiGetErrorString());
		}
		return new Model(scene);
	}
}
