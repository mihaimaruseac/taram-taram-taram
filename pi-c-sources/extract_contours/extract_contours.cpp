#include <iostream>
#include <string>

#include "Contour.h"

int main(int argc, const char * argv[])
{
	if (argc < 2)
	{
		std::cerr << "Usage: " << argv[0] << " <jpg file>" << std::endl;
		return 1;
	}

	std::cout << "Extracting contours from " << argv[1] << std::endl;

	Contour contour;

	if (contour.readImage(argv[1]) == false)
	{
		return 1;
	}

	contour.extractContours();

	contour.filterAndSmoothContours();

	std::string contour_file = std::string(argv[1]);
	contour_file.replace(contour_file.end()-4, contour_file.end(), "_contour.jpg");
	contour.writeImageToDisk(contour_file.c_str());

	contour.printContoursInfo();

	return 0;
}
