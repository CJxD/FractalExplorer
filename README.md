# Fractal Explorer
Authored by Chris Watts

## Building
Both ANT build files and shell files are provided to successfully build the project.
The ANT build.xml can build a JAR as well as compile the source, but build.bat and build.sh just compile and run.

All required libraries for this project are located under `lib`

## Usage
Upon running `main` found in `com.cjwatts.fractalexplorer.main.FractalExplorer`, you should see the main dialog with two fractal panels.
The larger one displays the fractal algorithm as specified by the fractal controls; the smaller displays the Julia set of the current algorithm.

By clicking on an area of the main fractal, you can select a point to 'explore'. The selected point is passed as the seed for the Julia algorithm, which will then update.

## Specification
Original spec is (was) found here: https://secure.ecs.soton.ac.uk/notes/comp1206/assn1-2013.html

### Extensions
The following extensions have been made past the specification:

  * Favourites are stored in favourites.xml
  * Image rendering is multithreaded.
  * A progress bar is shown on the main fractal for render progress.
  * Images are buffered for fast response times when the fractals are not modified.
  * Many fractal algorithms are provided, and writing your own is easily done through extending the FractalAlgorithm class.
  * The coordinate control spinners are logarithmic for pleasant scrolling.
  * Colours are smoothed.
  * Colour schemes are implemented, and can be easily modified in the program, or in favourites.xml
  * A full screen option is available.
  
### Known caveats

  * There is currently no GUI support for editing a colour scheme - you can only select pre-existing ones.
  * This was going to use OpenCL/CUDA, but because of the bulk of the native libraries required to ship with the program, it's so not worth it.
  * Favourites do not currently store the previous viewport as it cannot yet modify the swing components controlling it.
  * A viewport swapping feature exists, but due to last minute bugs, it was omitted.