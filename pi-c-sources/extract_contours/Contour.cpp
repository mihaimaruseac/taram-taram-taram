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
	std::cout << this->original_image.cols << " " << this->original_image.rows << "\n";
	return true;
}

void Contour::extractContours()
{
	cv::cvtColor(this->original_image, this->original_image, CV_BGR2GRAY);
	cv::threshold(this->original_image, this->original_image, 128, 255, CV_THRESH_BINARY);
	cv::Mat clone = this->original_image.clone();
	cv::findContours(clone, all_contours, CV_RETR_LIST, CV_CHAIN_APPROX_NONE);
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
	for(std::vector<std::vector<cv::Point> >::iterator it = this->contours.begin();
			it != this->contours.end();
			++it) {
		std::vector<cv::Point>::iterator prev = it->begin();
		std::vector<cv::Point>::iterator now = it->begin();
		std::vector<cv::Point>::iterator next = it->begin();
		std::vector<cv::Point>::iterator proper = it->begin();
		int sz = 0;

		/* Assume all countours have at least length 3 */
		++sz; ++now; ++next; ++next;

		while (proper != it->end()) {
			/* count points in current spline */
			while (now != it->end()) {
				++sz; ++now; ++next; ++prev;
				cv::Point dnp = *now - *prev;
				cv::Point dnn = *next - *now;
				if (dnp.dot(dnn) < 0)
					break;
			}

			//std::cout << sz << "\n";

			while (sz) {
				--sz;
				std::cout << proper->x << " " << proper->y << "\n";
				++proper;
			}
		}
	}
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
