.PHONY: all build clean

CC := g++
CPPFLAGS := -Wall --std=c++0x $(shell pkg-config --cflags opencv)
LDFLAGS := $(shell pkg-config --libs opencv)

TARGET = ./extract_contours
OBJS = Contour.o

all: $(TARGET)

build: $(TARGET)

$(TARGET): $(OBJS)

clean:
	rm -f $(OBJS) $(TARGET)
