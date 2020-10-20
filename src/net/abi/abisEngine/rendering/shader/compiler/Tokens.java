/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.shader.compiler;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL46;

/*
 * A Dedicated class to house all the GLSL Lang specific data for parsing.
 */
public class Tokens {
	/*
	 * Keywords
	 */
	static final String AE_INCLUDE_DIRECTIVE = "#include";
	static final String COMMENT_PREFIX = "//";
	static final String AE_IMPORT_DIRECTIVE = "#import";
	static final String AE_SHADER_FILE_TYPE = ".ae-shader";
	static final Map<Integer, String> fields = new HashMap<Integer, String>();
	static final Map<Integer, Integer> typeSizes = new HashMap<Integer, Integer>();

	/**
	 * GLSL Keywords.
	 */
	public static enum Keywords {
		ATTRIBUTE("attribute"), CONST("const"), UNIFORM("uniform"), BUFFER("buffer"), VARYING("varying"),
		LAYOUT("layout"), CENTROID("centroid"), FLAT("flat"), SMOOTH("smooth"), NOPERSPECTIVE("noperspective"),
		PATCH("patch"), SAMPLE("sample"), BREAK("break"), CONTINUE("continue"), DO("do"), FOR("for"), WHILE("while"),
		SWITCH("switch"), CASE("case"), DEFAULT("default"), IF("if"), ELSE("else"), SUBROUTINE("subroutine"), IN("in"),
		OUT("out"), INOUT("inout"), FLOAT("float"), DOUBLE("double"), INT("int"), VOID("void"), BOOL("bool"),
		TRUE("true"), FALSE("false"), INVARIANT("invariant"), DISCARD("discard"), RETURN("return"), MAT2("mat2"),
		MAT3("mat3"), MAT4("mat4"), DMAT2("dmat2"), DMAT3("dmat3"), DMAT4("dmat4"), MAT2X2("mat2x2"), MAT2X3("mat2x3"),
		MAT2X4("mat2x4"), DMAT2X2("dmat2x2"), DMAT2X3("dmat2x3"), DMAT2X4("dmat2x4"), MAT3X2("mat3x2"),
		MAT3X3("mat3x3"), MAT3X4("mat3x4"), DMAT3X2("dmat3x2"), DMAT3X3("dmat3x3"), DMAT3X4("dmat3x4"),
		MAT4X2("mat4x2"), MAT4X3("mat4x3"), MAT4X4("mat4x4"), DMAT4X2("dmat4x2"), DMAT4X3("dmat4x3"),
		DMAT4X4("dmat4x4"), VEC2("vec2"), VEC3("vec3"), VEC4("vec4"), IVEC2("ivec2"), IVEC3("ivec3"), IVEC4("ivec4"),
		BVEC2("bvec2"), BVEC3("bvec3"), BVEC4("bvec4"), DVEC2("dvec2"), DVEC3("dvec3"), DVEC4("dvec4"), UINT("uint"),
		UVEC2("uvec2"), UVEC3("uvec3"), UVEC4("uvec4"), LOWP("lowp"), MEDIUMP("mediump"), HIGHP("highp"),
		PRECISION("precision"), SAMPLER1D("sampler1D"), SAMPLER2D("sampler2D"), SAMPLER3D("sampler3D"),
		SAMPLERCUBE("samplerCube"), SAMPLER1DSHADOW("sampler1DShadow"), SAMPLER2DSHADOW("sampler2DShadow"),
		SAMPLERCUBESHADOW("samplerCubeShadow"), SAMPLER1DARRAY("sampler1DArray"), SAMPLER2DARRAY("sampler2DArray"),
		SAMPLER1DARRAYSHADOW("sampler1DArrayShadow"), SAMPLER2DARRAYSHADOW("sampler2DArrayShadow"),
		ISAMPLER1D("isampler1D"), ISAMPLER2D("isampler2D"), ISAMPLER3D("isampler3D"), ISAMPLERCUBE("isamplerCube"),
		ISAMPLER1DARRAY("isampler1DArray"), ISAMPLER2DARRAY("isampler2DArray"), USAMPLER1D("usampler1D"),
		USAMPLER2D("usampler2D"), USAMPLER3D("usampler3D"), USAMPLERCUBE("usamplerCube"),
		USAMPLER1DARRAY("usampler1DArray"), USAMPLER2DARRAY("usampler2DArray"), SAMPLER2DRECT("sampler2DRect"),
		SAMPLER2DRECTSHADOW("sampler2DRectShadow"), ISAMPLER2DRECT("isampler2DRect"), USAMPLER2DRECT("usampler2DRect"),
		SAMPLERBUFFER("samplerBuffer"), ISAMPLERBUFFER("isamplerBuffer"), USAMPLERBUFFER("usamplerBuffer"),
		SAMPLER2DMS("sampler2DMS"), ISAMPLER2DMS("isampler2DMS"), USAMPLER2DMS("usampler2DMS"),
		SAMPLER2DMSARRAY("sampler2DMSArray"), ISAMPLER2DMSARRAY("isampler2DMSArray"),
		USAMPLER2DMSARRAY("usampler2DMSArray"), SAMPLERCUBEARRAY("samplerCubeArray"),
		SAMPLERCUBEARRAYSHADOW("samplerCubeArrayShadow"), ISAMPLERCUBEARRAY("isamplerCubeArray"),
		USAMPLERCUBEARRAY("usamplerCubeArray"), STRUCT("struct");

		String word;

		Keywords(String word) {
			this.word = word;
		}

		@Override
		public String toString() {
			return word;
		}

		int length() {
			return word.length();
		}
	}

	public static enum Qualifiers {
		SHARED("shared"), PACKED("packed"), STD140("std140"), STD430("std430"), ROW_MAJOR("row_major"),
		COLUMN_MAJOR("column_major"), BINDING("binding"), OFFSET("offset"), ALIGN("align"), LOCATION("location"),
		COMPONENT("component"), INDEX("index"), TRIANGLES("triangles"), QUADS("quads"), ISOLINES("isolines"),
		EQUAL_SPACING("equal_spacing"), FRACTIONAL_EVEN_SPACING("fractional_even_spacing"),
		FRACTIONAL_ODD_SPACING("fractional_odd_spacing"), CW("cw"), CCW("ccw"), POINT_MODE("point_mode"),
		POINTS("points"), LINES("lines"), LINES_ADJACENCY("lines_adjacency"), ORIGIN_UPPER_LEFT("origin_upper_left"),
		PIXEL_CENTER_INTEGER("pixel_center_integer"), LOCAL_SIZE_X("local_size_x"), LOCAL_SIZE_Y("local_size_y"),
		LOCAL_SIZE_Z("local_size_z"), XFB_BUFFER("xfb_buffer"), XFB_STRIDE("xfb_stride"), XFB_OFFSET("xfb_offset"),
		VERTICES("vertices"), LINE_STRIP("line_strip"), TRIANGLE_STRIP("triangle_strip"), MAX_VERTICES("max_vertices"),
		STREAM("stream"), DEPTH_ANY("depth_any"), DEPTH_GREATER("depth_greater"), DEPTH_LESS("depth_less"),
		DEPTH_UNCHANGED("depth_unchanged");

		String qualifier;

		Qualifiers(String qualifier) {
			this.qualifier = qualifier;
		}

		@Override
		public String toString() {
			return qualifier;
		}

		int length() {
			return qualifier.length();
		}
	}

	/**
	 * Operators.
	 */
	public static enum Operators {
		PARANTHESIS_BEGIN("("), PARANTHESIS_END(")"), BRACKET_BEGIN("["), BRACKET_END("]"), DOT("."), INCREMENT("++"),
		DECREMENT("--"), BIT_NOT("~"), LOGICAL_NOT("!"), MULTIPLY("*"), DIVIDE("/"), MODULUS("%"), PLUS("+"),
		MINUS("-"), SHIFT_LEFT("<<"), SHIFT_RIGHT(">>"), LESS("<"), GREATER(">"), LESS_EQUAL("<="), GREATER_EQUAL(">="),
		EQUAL("=="), NOT_EQUAL("!="), BIT_AND("&"), BIT_XOR("^"), BIT_OR("|"), LOGICAL_AND("&&"), LOGICAL_XOR("^^"),
		LOGICAL_OR("||"), QUESTIONMARK("?"), COLON(":"), ASSIGN("="), ADD_ASSIGN("+="), SUB_ASSIGN("-="),
		MULTIPLY_ASSIGN("*="), DIVIDE_ASSIGN("/="), MODULUS_ASSIGN("%="), SHIFT_LEFT_ASSIGN("<<="),
		SHIFT_RIGHT_ASSIGN(">>="), BIT_AND_ASSIGN("&="), BIT_XOR_ASSIGN("^="), BIT_OR_ASSIGN("|="), COMMA(","),
		SEMICOLON(";"), CURLEY_BRACKET_BEGIN("{"), CURLEY_BRACKET_END("}");

		String op;

		Operators(String op) {
			this.op = op;
		}

		@Override
		public String toString() {
			return op;
		}

	}

}
