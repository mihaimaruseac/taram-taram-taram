#include <iostream>
#include <vector>
#include <iterator>

#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"

#include "Contour.h"

Contour::Contour(int threshold, int smooth_factor)
{
	this->threshold = threshold;
	this->smooth_factor = smooth_factor;
}

bool Contour::readImage(const char *file_path)
{
	this->original_image = cv::imread(file_path);
	if (!original_image.data) {
		std::cerr << "Image " << file_path << " not found!" << std::endl;
		return false;
	}
	return true;
}

void Contour::extractContours()
{
	cv::cvtColor(this->original_image, this->original_image, CV_BGR2GRAY);
	cv::threshold(this->original_image, this->original_image, 128, 255, CV_THRESH_BINARY);
	cv::findContours(this->original_image.clone(),
			all_contours, CV_RETR_LIST, CV_CHAIN_APPROX_NONE);
}

void Contour::filterAndSmoothContours()
{
	for(std::vector<std::vector<cv::Point> >::iterator it = all_contours.begin();
			it != all_contours.end();
			++it) {

		/* filter too small contours */
		if (it->size() < this->threshold)
			continue;

		this->contours.push_back(this->smoothContour(*it));
	}
}

std::vector<cv::Point> Contour::smoothContour(std::vector<cv::Point> contour)
{
	return contour;
}

void Contour::writeImageToDisk(const char *file_path)
{
	cv::Mat contour_image(this->original_image.size(), CV_8UC3, cv::Scalar(0,0,0));

	const int num_colours = 6;
	cv::Scalar colors[num_colours];

	colors[0] = cv::Scalar(255, 0, 0);
	colors[1] = cv::Scalar(0, 255, 0);
	colors[2] = cv::Scalar(0, 0, 255);
	colors[3] = cv::Scalar(0, 255, 255);
	colors[4] = cv::Scalar(255, 0, 255);
	colors[5] = cv::Scalar(255, 255, 0);

	for (size_t idx = 0; idx < this->contours.size(); idx++)
		cv::drawContours(contour_image, this->contours, idx, colors[idx % num_colours]);
	cv::imwrite(file_path, contour_image);
}

void Contour::printContoursInfo()
{
#if 0
	std::cout << this->contours.size() << " contours found." << std::endl;
	std::cout << "Lengths : ";
	for(std::vector<std::vector<cv::Point> >::iterator it = this->contours.begin();
			it != this->contours.end();
			++it)
		std::cout << it->size() << " ; ";
	std::cout << std::endl;
#endif
#if 0
	std::vector<cv::Point> smoothed_contour;

	/* Assuming all countours have at least length 3 */
	std::vector<cv::Point>::iterator prev = contour.begin();
	std::vector<cv::Point>::iterator now = contour.begin();
	smoothed_contour.push_back(*now);
	++now;
	std::vector<cv::Point>::iterator next = contour.begin();
	++next; ++next;

	std::cout << "MM begin\n";

	for(;
			next != contour.end();
			++prev, ++now, ++next) {
		smoothed_contour.push_back(*now);

		cv::Point dnp = *now - *prev;
		cv::Point dnn = *next - *now;

		if (dnp.dot(dnn) < 0)
			std::cout << "MM 42 here " << *prev << " " << *now
				<< " " << *next << " " << dnp << " " << dnn
				<< "::" << dnp.dot(dnn) << "\n";
	}
	smoothed_contour.push_back(*now);

	std::cout << "MM end\n";

	return smoothed_contour;
#endif
}

int Contour::getThreshold()
{
	return this->threshold;
}

void Contour::setThreshold(int threshold)
{
	this->threshold = threshold;
}

int Contour::getSmoothFactor()
{
	return this->smooth_factor;
}

void Contour::setSmoothFactor(int smooth_factor)
{
	this->smooth_factor = smooth_factor;
}

void Contour::updateContours(int threshold, int smooth_factor)
{
	this->threshold = threshold;
	this->smooth_factor = smooth_factor;
	this->filterAndSmoothContours();
}

std::vector<std::vector<cv::Point>> Contour::getContours()
{
	return this->contours;
}
