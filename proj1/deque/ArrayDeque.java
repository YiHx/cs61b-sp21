package deque;

public class ArrayDeque<T> {
    private T[] array;
    private int size;
    private int End;
    private int First;

    public ArrayDeque() {
        array = (T[]) new Object[8];
        size = 0;
        End = 0;
        First = 0;
    }

    public void resize(){
        if(size/array.length>1/4){
            T[] curr = (T[])new Object[array.length*2];
            int begin =First-1;
            int i=0;
            for(;begin<array.length;i++,begin++){
                curr[i]=array[begin];
            }
            for(int j=i+1,k=0;k<End;j++,k++){
                curr[j]=array[k];
            }
            array=curr;
        }
    }

    public void addFirst(T item){
        resize();
        if(array[0]==null){
            array[First]=item;
            First=array.length;
            End++;
            size++;
            return;
        }
        array[First]=item;
        size++;
        First--;

    }
    public void addLast(T item){
        resize();
        if(array[0]==null){
            array[End]=item;
            End++;
            size++;
            First=array.length;
            return;
        }
        array[End] = item;
        size++;
        End++;
    }

    public boolean isEmpty(){
        return size==0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        if(First+1!=array.length){
            for(int i=First+1;i<array.length;i++) {
                System.out.print(array[i] + " ");
            }
        }
        if(End-1>=0){
            for(int i=End-1;array[i]!=null;i++){
                System.out.print(array[i]+" ");
            }
        }
        System.out.print("\n");
    }

    public T removeFirst(){

    }


}

