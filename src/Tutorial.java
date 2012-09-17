

import OBJLoader.OBJParser;

import windowtemplate.Window;


public class Tutorial {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MultiplePrograms();
		//setExample(6);
	
	}
	private static Window setExample(int number){
		
		switch (number) {
		case 0:
			return new OneTriangle();
		case 1:
			return new TwoPositionsPlusColor();
		case 2:
			return new ThreeInterweavedCoordsAndColors();
		case 3:
			return new FourMatricesAsUniforms();
		case 4:
			return new FiveAnotherDimension();
		case 5:
			return new SixTextures();
		case 6:
			return new SevenToonShaders();
			
		default:
			System.out.println("Example number Not set, not launching");
			break;
		}
		return null;
		
	}

	
	
}
