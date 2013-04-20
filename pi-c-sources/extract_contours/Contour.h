#include <vector>

#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"

class Contour
{
	public:
		Contour(int threshold=100, int smooth_factor=1);

		// Citește o imagine dintr-un fișier
		bool readImage(const char *file_path);
		// Extrage toate contururile folosind OpenCV
		void extractContours();
		// Scrie o imagine din contururi
		void writeImageToDisk(const char *file_path);
		// Afișează la stdout informații despre contururi
		void printContoursInfo();

		std::vector<std::vector<cv::Point> > getContours();

		int getThreshold();
		void setThreshold(int threshold);
		int getSmoothFactor();
		void setSmoothFactor(int smooth_factor);

		std::vector<cv::Point> smoothContour(std::vector<cv::Point> contour);

		void updateContours(int threshold, int smooth_factor);
		void filterAndSmoothContours();

	private:
		// minimum number of points in a contour
		unsigned int threshold;
		// length of smoothing window
		int smooth_factor;
		cv::Mat original_image;
		std::vector<std::vector<cv::Point> > all_contours;
		std::vector<std::vector<cv::Point> > contours;
};
