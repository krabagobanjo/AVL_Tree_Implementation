import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * My AVL implementation.
 *
 * @author Kyle Rabago-Banjo
 */
public class AVL<T extends Comparable<T>> implements AVLInterface<T>,
       Gradable<T> {

    // Do not add additional instance variables
    private Node<T> root;
    private int size;

    public AVL() {
        size = 0;
    }

    @Override
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else {
            root = addNode(root, new Node<T>(data));
            size++;
        }
    }
/**
 * Recursive add helper
 * @param parent - parent of tree
 * @param newNode - node being added
 * @return root of balanced tree (see rebalance)
 */
    private Node<T> addNode(Node<T> parent, Node<T> newNode) {
        if (parent == null) {
            newNode.setHeight(0);
            newNode.setBalanceFactor(0);
            return newNode;
        } else if (newNode.getData().compareTo(parent.getData()) < 0) {
            parent.setLeft(addNode(parent.getLeft(), newNode));
        } else if (newNode.getData().compareTo(parent.getData()) > 0) {
            parent.setRight(addNode(parent.getRight(), newNode));
        } else {
            return parent; //do nothing
        }
        return rebalance(parent, newNode);
    }

    /**
     * Ensures balanced tree on add
     * @param parent of tree
     * @param compNode - added node
     * @return balanced parent
     */
    private Node<T> rebalance(Node<T> parent, Node<T> compNode) {
        int height = getHeight(parent);
        parent.setHeight(height);


        int balance = getBalance(parent);

        parent.setBalanceFactor(balance);

        if (balance > 1
                && compNode.getData().compareTo(parent.getLeft().getData())
                < 0) {
            return rightRotate(parent);
        } else if (balance > 1
                && compNode.getData().compareTo(parent.getLeft().getData())
                > 0) {
            parent.setLeft(leftRotate(parent.getLeft()));
            return rightRotate(parent);
        } else if (balance < -1
                && compNode.getData().compareTo(parent.getRight().getData())
                < 0) {
            parent.setRight(rightRotate(parent.getRight()));
            return leftRotate(parent);
        } else if (balance < -1
                && compNode.getData().compareTo(parent.getRight().getData())
                > 0) {
            return leftRotate(parent);
        }
        return parent;
    }

    /**
     * Rebalance for remove
     * @param parent
     * @return balanced parent
     */
    private Node<T> rebalance(Node<T> parent) {
        int height = getHeight(parent);
        parent.setHeight(height);


        int balance = getBalance(parent);

        parent.setBalanceFactor(balance);

        if (balance > 1) {
            return rightRotate(parent);
        } else if (balance > 1) {
            parent.setLeft(leftRotate(parent.getLeft()));
            return rightRotate(parent);
        } else if (balance < -1) {
            parent.setRight(rightRotate(parent.getRight()));
            return leftRotate(parent);
        } else if (balance < -1) {
            return leftRotate(parent);
        }
        return parent;
    }

    /**
     * Performs left rotation
     * @param node
     * @return rotated tree
     */
    private Node<T> leftRotate(Node<T> node) {
        Node<T> newRoot = node.getRight();
        Node<T> child = null;
        if (newRoot != null) {
            if (newRoot.getLeft() == null) {
                child = null;
            } else {
                child = newRoot.getLeft();
            }
            newRoot.setLeft(node);
            node.setRight(child);

            newRoot.setHeight(getHeight(newRoot));
            node.setHeight(getHeight(node));
            newRoot.setBalanceFactor(getBalance(newRoot));
            node.setBalanceFactor(getBalance(node));
        }
        return newRoot;
    }

    /**
     * Performs right rotation
     * @param node
     * @return rotated tree
     */
    private Node<T> rightRotate(Node<T> node) {
        Node<T> newRoot = node.getLeft();
        Node<T> child = null;
        if (newRoot != null) {
            if (newRoot.getRight() == null) {
                child = null;
            } else {
                child = newRoot.getRight();
            }
            newRoot.setRight(node);
            node.setLeft(child);

            newRoot.setHeight(getHeight(newRoot));
            node.setHeight(getHeight(node));
            newRoot.setBalanceFactor(getBalance(newRoot));
            node.setBalanceFactor(getBalance(node));
        }
        return newRoot;
    }

    /**
     * Get balance factor
     * @param node
     * @return balance factor of node
     */
    private int getBalance(Node<T> node) {
        int balance;
        if (node.getLeft() != null && node.getRight() != null) {
            balance = (node.getLeft().getHeight() + 1)
                    - (node.getRight().getHeight() + 1);
        } else if (node.getLeft() != null && node.getRight() == null) {
            balance = node.getLeft().getHeight() + 1;
        } else if (node.getLeft() == null && node.getRight() != null) {
            balance = 0 - (node.getRight().getHeight() + 1);
        } else {
            balance = 0;
        }
        return balance;
    }

    @Override
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else {
            Node<T> returnVal = new Node<T>(null);
            root = removal(root, data, returnVal);
            return returnVal.getData();
        }
    }

    /**
     * Recursive removal helper
     * @param trav - traverse through tree
     * @param data - data being removed
     * @param ret - node with data being removed
     * @return data being removed
     */
    private Node<T> removal(Node<T> trav, T data, Node<T> ret) {
        if (trav == null) {
            return null;
        }
        if (data.compareTo(trav.getData()) > 0) {
            trav.setRight(removal(trav.getRight(), data, ret));
        } else if (data.compareTo(trav.getData()) < 0) {
            trav.setLeft(removal(trav.getLeft(), data, ret));
        } else {
            ret.setData(trav.getData());
            if (trav.getLeft() != null && trav.getRight() != null) {
                Node<T> succ = findSuccessor(trav);
                trav.setData(succ.getData());
            } else if (trav.getLeft() == null) {
                trav = trav.getRight();
            } else {
                trav = trav.getLeft();
            }
            size--;
        }

        if (trav == null) {
            return trav;
        } else {
            return rebalance(trav);
        }
    }

    /**
     * Find successor for removal
     * @param trav - tree traversal pointer
     * @return successor
     */
    private Node<T> findSuccessor(Node<T> trav) {
        Node<T> prev = null;
        Node<T> ret = null;
        trav = trav.getRight();
        while (trav.getLeft() != null) {
            prev = trav;
            trav = trav.getLeft();
        }
        ret = trav;
        if (prev == null) {
            trav.setLeft(trav.getLeft());
        } else {
            prev.setLeft(trav.getLeft());
        }
        return ret;
    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        Node<T> trav = root;
        if (trav == null) {
            return null;
        } else {
            boolean loop = true;
            while (loop) {
                if (trav.getData().compareTo(data) < 0) {
                    if (trav.getRight() == null) {
                        return null;
                    } else {
                        trav = trav.getRight();
                    }
                } else if (trav.getData().compareTo(data) > 0) {
                    if (trav.getLeft() == null) {
                        return null;
                    } else {
                        trav = trav.getLeft();
                    }
                } else if (trav.getData().equals(data)) {
                    return trav.getData();
                } else {
                    loop = false;
                }
            }
        }
        return null;
    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        T ret = get(data);
        if (ret == null) {
            return false;
        } else {
            return ret.equals(data);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> preorder() {
        ArrayList<T> ret = new ArrayList<T>(size);
        preorderHelp(root, ret);
        return ret;
    }

    /**
     * Recursive preorder helper
     * @param trav (starting point)
     * @param list
     */
    private void preorderHelp(Node<T> trav, ArrayList<T> list) {
        if (trav == null) {
            return;
        } else {
            list.add(trav.getData());
            preorderHelp(trav.getLeft(), list);
            preorderHelp(trav.getRight(), list);
        }
    }


    @Override
    public List<T> postorder() {
        ArrayList<T> ret = new ArrayList<T>(size);
        postorderHelp(root, ret);
        return ret;
    }

    /**
     * Recursive postorder helper
     * @param trav (starting point)
     * @param list
     */
    private void postorderHelp(Node<T> trav, ArrayList<T> list) {
        if (trav == null) {
            return;
        } else {
            postorderHelp(trav.getLeft(), list);
            postorderHelp(trav.getRight(), list);
            list.add(trav.getData());
        }
    }

    @Override
    public List<T> inorder() {
        ArrayList<T> ret = new ArrayList<T>(size);
        inorderHelp(root, ret);
        return ret;
    }

    /**
     * Recursive inorder helper
     * @param trav (starting point)
     * @param list
     */
    private void inorderHelp(Node<T> trav, ArrayList<T> list) {
        if (trav == null) {
            return;
        } else {
            inorderHelp(trav.getLeft(), list);
            list.add(trav.getData());
            inorderHelp(trav.getRight(), list);
        }
    }

    @Override
    public List<T> levelorder() {
        LinkedList<Node<T>> temp = new LinkedList<Node<T>>();
        ArrayList<T> ret = new ArrayList<T>(size);
        Node<T> trav = root;
        while (trav != null) {
            ret.add(trav.getData());
            if (trav.getLeft() != null) {
                temp.add(trav.getLeft());
            }
            if (trav.getRight() != null) {
                temp.add(trav.getRight());
            }
            if (temp.size() > 0) {
                trav = temp.remove();
            } else {
                trav = null;
            }
        }
        return ret;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int height() {
        Node<T> trav = root;
        return getHeight(trav);
    }

    /**
     * Recursive height helper
     * @param ht
     * @return height
     */
    private int getHeight(Node<T> ht) {
        if (ht == null) {
            return -1;
        } else {
            return 1 + Math.max(
                    getHeight(ht.getLeft()), getHeight(ht.getRight()));
        }
    }

    @Override
    public Node<T> getRoot() {
        return root;
    }
}
