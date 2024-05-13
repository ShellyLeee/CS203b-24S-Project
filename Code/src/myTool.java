public class myTool {

    public static int[] sortIndex(double[] arr) {
        int[] index = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            index[i] = i;
        }
        for (int i = 1; i < arr.length; i++) {
            double key = arr[i];
            int keyIndex = index[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                index[j + 1] = index[j];
                j = j - 1;
            }
            arr[j + 1] = key;
            index[j + 1] = keyIndex;
        }
        return index;
    }

    public static int[] insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
        return arr;

    }

}
