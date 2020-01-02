import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sorts a huge array using external sort
 * @author Justine Huynh
 * 03/06/2019
 */
public class ExternalSort {

    private static final int INSERTION_SORT_THRESHOLD = 60;
    private static int chunkNum = 1;

    /**
     * Constructs an object of type external sort with no initial variables
     */
    public ExternalSort()
    {
    }

    /**
     * Sorts a huge array using external sort and writing it down onto an output file
     * @param inputFile the "file" to be read that has the given array
     * @param outputFile the "file" to write the whole sorted array
     * @param n the size of the array
     * @param k the chunks; divides the array into groups of k, where k > 1. size of my memory
     */
    public void externalSort(String inputFile, String outputFile, int n, int k)
    {
        makeKChunk(inputFile, k, n);
        Path myPath = Paths.get(inputFile);

        Path tempPathOdd = Paths.get("temp1");
        Path tempPathEven = Paths.get("temp2");
        Path sortedPath = Paths.get("temp" + String.valueOf((int) Math.ceil( (double)n/k) + 1));
        merge(sortedPath, tempPathOdd, tempPathEven);
        int newSortedIndex = (int) Math.ceil((double) n/k) + 1;

        for (int tempFile = 3; tempFile <= Math.ceil((double)n/k); tempFile ++) {
            tempPathOdd = sortedPath;
            tempPathEven = Paths.get("temp" + tempFile);
            newSortedIndex ++;
            sortedPath = Paths.get("temp" + newSortedIndex);
            merge(sortedPath, tempPathOdd, tempPathEven);
        }

        Path outputPath = Paths.get(outputFile);
        writingToFile(sortedPath, outputPath, "inputFile");
    }

    /**
     * Reads the input file and makes chunks of size k..also sorts the mini K array and writes
     * it into a new Path.
     * @param inputFile the given input file that has unsorted float numbers
     * @param k the size of chunk
     * @param n the size of the "array" (the number of float numbers in inputFile)
     */
    private void makeKChunk(String inputFile,int k, int n) {

        Path inputPath = Paths.get(inputFile);
        Path temp = Paths.get("temp" + chunkNum);
        BufferedReader br = null;
        BufferedWriter bw = null;
        String line;
        float [] myKChunk = new float[k];
        int numLastKChunk = 0;

        try {
            br = Files.newBufferedReader(inputPath);
            bw = Files.newBufferedWriter(temp);
            line = br.readLine();
            while (line != null) {
                for (int fileIndex = 1; fileIndex <= n; fileIndex ++) {
                    myKChunk[(fileIndex - 1) % k] = Float.parseFloat(line);
                    line = br.readLine();
                    if (((fileIndex) % k == 0 || fileIndex == n || (n-fileIndex) < k - 1) && myKChunk[myKChunk.length - 1] != 0.0) {
                        // sort
                        sort(myKChunk);

                        // copy to file
                        for (float value : myKChunk) {
                            bw.write(String.valueOf(value));
                            bw.newLine();
                        }

                        // prepare for next temp File
                        if (fileIndex != n)
                        {
                            bw.close();
                            chunkNum++;
                            temp = Paths.get("temp" + chunkNum);
                            bw = Files.newBufferedWriter(temp);
                        }

                        // creating the last K chunk in case the last chunk size < k
                        if ((n - fileIndex) < k && numLastKChunk == 0) {
                            float[] emptyArray = new float[n - fileIndex];
                            myKChunk = emptyArray;
                            numLastKChunk++;
                        }
                        else {
                            float[] emptyArray = new float[k];
                            myKChunk = emptyArray; // myKChunk is now empty! Full of 0s!
                        }
                    }
                }
                    }
        }
        catch (IOException e) {
            System.err.println("Oops! IOException! There is no path.");
        }
        finally {
            try {
                if (bw != null){
                    bw.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.err.println("Oops! Your BufferedWriter or BufferedReader is null!");
            }
        }
    }

    /**
     * Sorts the array. Depending on the size, method is either insertion sort or Quick Sort
     * @param myArray the given float array
     */
    private void sort(float[] myArray) {
        if (myArray.length <= INSERTION_SORT_THRESHOLD) {
            insertionSort(myArray);
        }
        else {
            quickSort(myArray, 0, myArray.length - 1);
        }
    }

    /**
     * Sorts a given float array by Insertion Sort; finding newer smaller values
     * to insert into their correct place
     * @param myArray the given float array
     */
    private void insertionSort(float[] myArray) {
        for (int index = 1; index < myArray.length; index ++)
        {
            float minValue = myArray[index]; // create the temp variable for current value
            int prevIndex = index - 1;
            // Checks to see if any elements in myArray[0, ... index - 1]
            // if greater than the temp/minValue.
            while (prevIndex >= 0 && myArray[prevIndex] > minValue)
            {
                // if the previous value is greater than the temp/current value, switch
                myArray[index] = myArray[index - 1];
                prevIndex --; // keep going back
                index --;
            }
            myArray[prevIndex + 1] = minValue; // update where the temp/minValue is
        }
    }

    /**
     * Sorts a given float array using Quick Sort technique
     * @param arr the given float array
     * @param left the leftmost index
     * @param right the rightmost index
     */
    private void quickSort(float[] arr, int left, int right) {

        if (left < right) {
            int p = partition(arr, left, right);
            quickSort(arr, left, p - 1);
            quickSort(arr, p + 1, right);
        }
    }

    /**
     * Partitions, or splits, a given array
     * @param arr the given array
     * @param left the left index
     * @param right the right index
     * @return the new pivot
     */
    private int partition(float[] arr, int left, int right) {
        if (left < right) {
            int pivot = left;
            int i = left + 1; // Avoids re-sorting the pivot
            int j = right;
            while (i < j) {
                while (i <= right && arr[i] <= arr[pivot]) {
                    ++i;
                }
                while (j >= i && arr[j] > arr[pivot]) {
                    --j;
                }
                if (i <= right && i < j) {
                    swap(arr, i, j);
                }
            }
            swap(arr, pivot, j);  // pivot to the middle
            return j;
        }
        return left;
    }

    /**
     * Swaps 2 elements in an array
     * @param myArray the float array
     * @param i the previous, first index
     * @param j the subsequent, second index
     */
    private void swap(float[] myArray, int i, int j)
    {
        float temp = myArray[i];
        myArray[i] = myArray[j];
        myArray[j] = temp;
    }

    /**
     * Merges 2 files into one file. End product is a path with sorted float numbers
     * @param sortedPath the path to a file that will eventually have a sorted array
     * @param pathOne one of 2 files/path that has a list of sorted float numbers
     * @param pathTwo one of 2 files/path that has a list of sorted float numbers
     */
    private void merge(Path sortedPath, Path pathOne, Path pathTwo){
        BufferedReader brOne = null;
        BufferedReader brTwo = null;
        BufferedWriter bw = null;
        String line1;
        String line2;

        try {
            bw = Files.newBufferedWriter(sortedPath);
//            brOne = Files.newBufferedReader(pathOne);
//            brOne = new BufferedReader(new FileReader(pathOneName)); // input a text name
            brOne = Files.newBufferedReader(pathOne);
//            brTwo = new BufferedReader(new FileReader(pathTwoName));
            brTwo = Files.newBufferedReader(pathTwo);
            line1 = brOne.readLine();
            line2 = brTwo.readLine();

            while (line1!= null && line2 != null) {
                if (Float.parseFloat(line1) <= Float.parseFloat(line2)) {
                    bw.write(line1);
                    bw.newLine();
                    line1 = brOne.readLine(); // advance line1
                }
                else {
                    bw.write(line2);
                    bw.newLine();
                    line2 = brTwo.readLine(); // advance line2
                }
            }

            // copies all remaining extra elements from arrays left and right into a Path/File
            while (line1 != null) {
                bw.write(line1);
                bw.newLine();
                line1 = brOne.readLine(); // update line1
            }

            while (line2 != null) {
                bw.write(line2);
                bw.newLine();
                line2 = brTwo.readLine();
            }
        }
        catch (IOException e) {
            System.err.println("Oops! An IO Exception! When we tried to merge 2 files into another.");
        }
        catch (NullPointerException e) {
            System.out.println("Oops! You don't have any more values in File!");
        }
        finally {
            try {
                if(bw != null) {
                    bw.close();
                }
                if (brOne != null) {
                    brOne.close();
                }
                if (brTwo != null) {
                    brTwo.close();
                }
            }
            catch(IOException e){
                    System.out.println("Error in closing the BufferedWriter! Your BufferedWriter may be null.");
            }
        }
    }

    /**
     * Writes the contents of one file into another file by using Path
     * @param sortedPath the path file that has the sorted array
     * @param outputPath the output file to be written
     * @param inputFile a dummy variable
     */
    public void writingToFile(Path sortedPath, Path outputPath, String inputFile) {
        BufferedReader brSorted = null;
        BufferedWriter bwOutput = null;
        String line;
        chunkNum ++;
        try {
            brSorted = Files.newBufferedReader(sortedPath);
            bwOutput = Files.newBufferedWriter(outputPath); // writer, write to outputFile
            line = brSorted.readLine();
            while (line != null) {
                bwOutput.write(line);
                bwOutput.newLine();
                line = brSorted.readLine();
            }
        }
        catch(IOException e){
            System.err.println("Cannot write to file.");
        }
        catch (NullPointerException e) {
            System.err.println("Sorry! Line is null!");

        }
        finally {
            try {
                if (bwOutput != null) {
                    bwOutput.close();
                }
                if (brSorted != null) {
                    brSorted.close();
                }
            }
            catch (IOException e) {
                System.err.println("Oops! A null-pointer exception! Your temp file actually has nothing in it!");
            }
        }
    }

    public static void main(String[] args) {
//        ExternalSort myExSort = new ExternalSort();
//        myExSort.externalSort("src\\inputFile", "outputFile", 12, 3);
        Path inputPath = Paths.get("src\\inputFile");
//        System.out.println(inputPath);
//        Path outputPath = Paths.get("outputFile");

    }


}
