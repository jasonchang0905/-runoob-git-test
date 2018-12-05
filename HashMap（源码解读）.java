/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Hash table based implementation of the <tt>Map</tt> interface.  This				//��ϣ���ǻ���map�ӿ���ʵ�ֵģ�����ʵ�ַ�ʽ�ṩ�����п�ѡ���ԵĲ�������������յļ�ֵ��
 * implementation provides all of the optional map operations, and permits			��hashmap��hashtable�����ϲ�࣬����hashmap���̲߳���ȫ������ռ�ֵ�����⣩hashmap����
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>			��֤map��˳�򣬸����ܱ�֤�����Ԫ��˳��һ�²���
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 *
 * <p>This implementation provides constant-time performance for the basic			//hashmap��get��put������˵�кܸߵ�����,����hash�㷨������Ԫ��ɢ�л�����ͬ��Ͱ�У���������
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function			���ĵ�ʱ������鳤�Ⱥ������ȳɱ�����ϵ������ڱ�������Ҫ�����ܸߵ�����£��Ͳ�Ҫ����ʼ����
 * disperses the elements properly among the buckets.  Iteration over				���õĹ��߻���װ���������õĹ���
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 *
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its			//������������Ӱ��һ��hashmap�����ܣ����Ƿֱ��ǳ�ʼ�������ͼ������ӣ�����ָ���ǹ�ϣ����Ͱ��������
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The				��ʼ�������ǹ�ϣ��մ���ʱ�������������������������������Զ����ӵ�����£����жϹ�ϣ���Ƿ�����
 * <i>capacity</i> is the number of buckets in the hash table, and the initial		����װ�����Ӻ������ĳ˻���Ҫ����rehash��Ҳ����˵�������ڲ��ṹ���ع���rehash����������ԭ��������
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 *
 * <p>As a general rule, the default load factor (.75) offers a good				//��ʱ����ռ����������£�ѡ��Ĭ�ϵļ�������0.75����������̫���С�ռ俪������ͬʱ����߲��ҳɱ�
 * tradeoff between time and space costs.  Higher values decrease the				����Ҫ����hashmap��get��put�Ĳ��������ó�ʼ������װ�������ڳ�ʼ����ʱ��Ҫ���ǣ���Ϊrehash�����Ĵ���
 * space overhead but increase the lookup cost (reflected in most of				�����ʼ���������������Ŀ�����Ը������ӣ�rehash���������ᷢ��--���仰˵���ǲ���������
 * the operations of the <tt>HashMap</tt> class, including
 * <tt>get</tt> and <tt>put</tt>).  The expected number of entries in
 * the map and its load factor should be taken into account when
 * setting its initial capacity, so as to minimize the number of
 * rehash operations.  If the initial capacity is greater than the
 * maximum number of entries divided by the load factor, no rehash
 * operations will ever occur.
 *
 * <p>If many mappings are to be stored in a <tt>HashMap</tt>						//��������ļ�ֵ�Դ洢��HashMapʵ���У������㹻��ĳ�ʼ��������initial capacity����
 * instance, creating it with a sufficiently large capacity will allow					�����ó�ʼ��������initial capacity������HashMapʵ���Զ����������Ӹ�Ч.
 * the mappings to be stored more efficiently than letting it perform					��ע�⣬����ʹ��key��ɢ��ֵ��hashCode()��һ���ļ�ֵ�� �ή�� �Ըö�ʵ���Ĳ���Ч��.
 * automatic rehashing as needed to grow the table.  Note that using					Ϊ�˸�������Ӱ�죬key�����ʵ��Comparable�ӿڣ�������ܹ���������Ӱ��.
 * many keys with the same {@code hashCode()} is a sure way to slow
 * down performance of any hash table. To ameliorate impact, when keys
 * are {@link Comparable}, this class may use comparison order among
 * keys to help break ties.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>			//��ע�����ʵ�֣�HashMap����  ���첽��.��������߳�ͬʱʹ��һ��hashMapʵ�������ٻ���
 * If multiple threads access a hash map concurrently, and at least one of			һ���̻߳��޸����hashMapʵ���Ľṹ.��ˣ�����Ҫ���Ӷ�����첽��װ��һ���ṹ�Ե��޸İ�
 * the threads modifies the map structurally, it <i>must</i> be						���κ����ӻ���ɾ����ֵ�ԵĲ����������޸����е�һ����ֵ�Ե�ֵ���������ǽṹ�Ե��޸ģ���
 * synchronized externally.  (A structural modification is any operation			��ͨ������һЩ��Ȼ��װmap�Ķ�����첽���.
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 *
 * If no such object exists, the map should be "wrapped" using the					//��������Ķ��󲢲�����,map�����Ӧ��ʹ��Collections.synchronizedMap 
 * {@link Collections#synchronizedMap Collections.synchronizedMap}					{@link Collections#synchronizedMap Collections.synchronizedMap}���� �����а���.
 * method.  This is best done at creation time, to prevent accidental				�����������ڴ���Map����ʱ���У���ֹ�����Map�������������ķ������Ĳ���.   
 * unsynchronized access to the map:<pre>											������Map m = Collections.synchronizedMap(new HashMap(...));��
 *   Map m = Collections.synchronizedMap(new HashMap(...));</pre>
 *
 * <p>The iterators returned by all of this class's "collection view methods"		//���������ᱻ����ļ�����ͼ���������� fail-fast���ƣ�
 * are <i>fail-fast</i>: if the map is structurally modified at any time after			fail-fast���ƣ�������map�����ڴ�������������κ�ʱ����
 * the iterator is created, in any way except through the iterator's own				��{@link ConcurrentModificationException}���������˽ṹ�Ըı䣨���˵����� remove �ķ�ʽ�⣩��
 * <tt>remove</tt> method, the iterator will throw a								��������������׳��쳣����ˣ�����Զ��߳�ͬ�¶�map��������޸Ĳ���ʱ��������������ټ���ʧ�ܣ�
 * {@link ConcurrentModificationException}.  Thus, in the face of concurrent		������ð����δ����ĳ����ȷ����ʱ���������ȷ����Ӱ��ķ���.
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed			//��ע�⣬ ��������fail-fast���Ʋ��ܱ���֤��һ����˵���������ڽ����첽�Ľṹ�޸Ĳ����У�
 * as it is, generally speaking, impossible to make any hard guarantees in the		���������κ�ȷ���ı�֤.������Fail-fastʱ�����������ᾡ���׳��쳣��ConcurrentModificationException����
 * presence of unsynchronized concurrent modification.  Fail-fast iterators			��ˣ������ڴ����в�����������쳣��ConcurrentModificationException����ȷ���������ȷ��.
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.			��������fail-fast����Ӧ�ñ���������bug��̽��.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.2
 */
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;			//���л���id�Ƿ����л���Ψһ��ʶ

    /*
     * Implementation notes.													//ע������
     *
     * This map usually acts as a binned (bucketed) hash table, but				//mapһ������µ���һ����ϣ��ʹ�ã���Ԫ�صĸ����Ƚ϶��ʱ�򣬾Ͳ���ʹ��hashtable��
     * when bins get too large, they are transformed into bins of				����ת��Ϊ������ڵ㣬�ṹ������treemap��Ԫ�ع���ʱ�����ֽṹ���ڲ��ҵ�Ч�ʸ��ߣ�
     * TreeNodes, each structured similarly to those in							�������ǰ�Ľṹ�������̽������ӳ�
     * java.util.TreeMap. Most methods try to use normal bins, but
     * relay to TreeNode methods when applicable (simply by checking
     * instanceof a node).  Bins of TreeNodes may be traversed and
     * used like any others, but additionally support faster lookup
     * when overpopulated. However, since the vast majority of bins in
     * normal use are not overpopulated, checking for existence of
     * tree bins may be delayed in the course of table methods.
     *
     * Tree bins (i.e., bins whose elements are all TreeNodes) are				//û���ṩcomparable����ӿڵ�ʵ�֣���ô��ʹ��hashcode���������򣬵�������������У�
     * ordered primarily by hashCode, but in the case of ties, if two				������ڵ��Ԫ�أ����ʵ����ͬһ��comparable�ӿڣ����������ǵ�compareTo�������Ƚϴ�С
     * elements are of the same "class C implements Comparable<C>",					��ǡ����ʹ��hashcode()��������������ϵ��½�����Ϊ���keys����һ��hashcode������
     * type then their compareTo method is used for ordering. (We					�ͻ�ȽϺܳ�ʱ�䣨�����Щ�������ʣ����Ǿ�Ӧ��ȥ����ʱ����ռ�����ƣ�����Ψһ��֪�����
     * conservatively check generic types via reflection to validate				�Ƕ���һ�㲻�õĳ�����룬��ʵҲûʲô����
     * this -- see method comparableClassFor).  The added complexity
     * of tree bins is worthwhile in providing worst-case O(log n)
     * operations when keys either have distinct hashes or are
     * orderable, Thus, performance degrades gracefully under
     * accidental or malicious usages in which hashCode() methods
     * return values that are poorly distributed, as well as those in
     * which many keys share a hashCode, so long as they are also
     * Comparable. (If neither of these apply, we may waste about a
     * factor of two in time and space compared to taking no
     * precautions. But the only known cases stem from poor user
     * programming practices that are already so slow that this makes
     * little difference.)
     *
     * Because TreeNodes are about twice the size of regular nodes, we			//��ΪTreeNodes����������ͨ�ڵ��С����ʹ����������Ҫȷ�����㹻��Ķ�ڵ�����
     * use them only when bins contain enough nodes to warrant use					�����ǻ�ԭʱ���Ƴ����ߵ������ͻ��÷ǳ�С
     * (see TREEIFY_THRESHOLD). And when they become too small (due to			�ڷֲ�ʽ��hashcode��tree bins������ʹ�õ�����������£����hashcode��Ͱ�еĽڵ�ʲ��ɷֲ�
     * removal or resizing) they are converted back to plain bins.  In			(http://en.wikipedia.org/wiki/Poisson_distribution) 0.75��Ĭ�Ϸ�ֵ���0.5�������������ȵ����нϴ�Ĳ���
     * usages with well-distributed user hashCodes, tree bins are				���Է��Ԥ�����б�Ĵ�СkΪexp(-0.5) * pow(0.5, k) /factorial(k) ��һ��ֵΪ��
     * rarely used.  Ideally, under random hashCodes, the frequency of
     * nodes in bins follows a Poisson distribution
     * (http://en.wikipedia.org/wiki/Poisson_distribution) with a
     * parameter of about 0.5 on average for the default resizing
     * threshold of 0.75, although with a large variance because of
     * resizing granularity. Ignoring variance, the expected
     * occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
     * factorial(k)). The first values are:
     *
     * 0:    0.60653066
     * 1:    0.30326533
     * 2:    0.07581633
     * 3:    0.01263606
     * 4:    0.00157952
     * 5:    0.00015795
     * 6:    0.00001316
     * 7:    0.00000094
     * 8:    0.00000006
     * more: less than 1 in ten million
     *
     * The root of a tree bin is normally its first node.  However,				//���ṹ�ĸ��ڵ�һ����˵�����ĵ�һ���ڵ㣬Ȼ������ʱ��Ʃ�磨Iterator.remove������������¸��ڵ������
     * sometimes (currently only upon Iterator.remove), the root might			��������ˣ����ǿ��Իָ�����ĸ�����
     * be elsewhere, but can be recovered following parent links
     * (method TreeNode.root()).
     *
     * All applicable internal methods accept a hash code as an					//���е��ڲ�����������hash������Ϊ������ͨ���ɹ��������ṩ�������������ٲ��ظ�����hashcode������»������
     * argument (as normally supplied from a public method), allowing			������ڲ�����Ҳ���ܱ�Ĳ�����������һ����ǰ�ı����������ݻ��߻�ԭʱ�п������±�Ҳ�����Ǿɱ�
     * them to call each other without recomputing user hashCodes.
     * Most internal methods also accept a "tab" argument, that is
     * normally the current table, but may be a new or old one when
     * resizing or converting.
     *
     * When bin lists are treeified, split, or untreeified, we keep			   //��Ͱ�����������ṹ �ָ�����Ƿ���״�ṹ ������ά�����Ǵ�ȡ/������˳�����ǵ���iterator.remove����
     * them in the same relative access/traversal order (i.e., field			������ʱʹ�ñȽ�������֤˳�򣨻��߾����ܵ�����Ҫ��ͨ��ƽ��ԭ��
     * Node.next) to better preserve locality, and to slightly					������tie-breakers���ȽϺ�����hashcode
     * simplify handling of splits and traversals that invoke
     * iterator.remove. When using comparators on insertion, to keep a
     * total ordering (or as close as is required here) across
     * rebalancings, we compare classes and identityHashCodes as
     * tie-breakers.
     *
     * The use and transitions among plain vs tree modes is						//һ��ģʽ����״ģʽ֮���ת��ͨ���õ�����LinkedHashMap
     * complicated by the existence of subclass LinkedHashMap. See				��μ����涨��Ҫ�ڲ���ʱ���õ�hook����
     * below for hook methods defined to be invoked upon insertion,				ȥ���ͷ���������LinkedHashMap������֮��Ķ�����
     * removal and access that allow LinkedHashMap internals to					����ҲҪ��mapʵ�����ݸ�һЩ���ܴ����½ڵ��ʵ�÷�����
     * otherwise remain independent of these mechanics. (This also
     * requires that a map instance be passed to some utility methods
     * that may create new nodes.)
     *
     * The concurrent-programming-like SSA-based coding style helps				//���б�������SSA�ı��뷽ʽ�����ڱ�������������е�Ť����ָ�������
     * avoid aliasing errors amid all of the twisty pointer operations.
     */

    /**
     * The default initial capacity - MUST be a power of two.					//HashMap�ĳ�ʼ���������2�ı���
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16				//HashMap��Ĭ�ϳ�ʼ������16 1<<4 

    /**
     * The maximum capacity, used if a higher value is implicitly specified		//���������Ȼû����ȷ�涨�����Ǳ�����2�ı��������ܴ���2��30�η�
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;								//���������2��30�η�

    /**
     * The load factor used when none specified in constructor.					//��������Ҳ�������ݵķ�ֵΪ0.75
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The bin count threshold for using a tree rather than list for a			//1.8��������ϣ��ײ��������ϴﵽ8���ڵ�ʱҪ�������ع�Ϊ���������ѯ��ʱ�临�Ӷ�
     * bin.  Bins are converted to trees when adding an element to a			��Ϊ0(logN)
     * bin with at least this many nodes. The value must be greater				
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a			//��Ͱ�еļ�ֵ������6~8֮��ʱ����Ҫ�����
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.				//��Ͱ��������64ʱ����תΪ�����
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     * Basic hash bin node, used for most entries.  (See below for				//�����Ĺ�ϣ��ڵ������ڴ������������
     * TreeNode subclass, and in LinkedHashMap for its Entry subclass.)
     */
    static class Node<K,V> implements Map.Entry<K,V> {							//������hashmap�ļ�����ɲ��֣�1���ڵ�Ĺ�ϣֵ��2��key��3��value��
        final int hash;																4��ָ����һ���ڵ��key��value�Ĺ�ϣֵȥ���
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {									// ����value����������������newValue�浽��ԭ����value�oldvalue�������ԭ����ֵ
            V oldValue = value;													���ڷ���������º��ֵ
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {									//boolean������map.entity��map�е��ڲ��ӿڣ�����ȡmap�е�ĳ�Լ�ֵ���ٵ���get��
            if (o == this)															�ֱ��жϼ�ֵ�Ƿ����
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    /* ---------------- Static utilities -------------- */

    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash			//�����hash����ʱ����ΪĿǰ��table������2�ı������ڼ����±��ʱ��ʹ��8λ����
     * to lower.  Because the table uses power-of-two masking, sets of			����n-1��&hash����������������ײ����Ϊʲô��ô˵�أ�����˼��һ�£���n-1Ϊ15��0x1111��ʱ��
     * hashes that vary only in bits above the current mask will				��ɢ��������Ч��ֻ�ǵ�4bit����Чֵ����Ȼ������ײ����ˣ����������һ����ȫ��ֵķ���
     * always collide. (Among known examples are sets of Float keys				���ۺ����ٶȣ����ã������������ǰѸ�16bit�͵�4bit�����һ�£�����߻����͵���Ϊ����
     * holding consecutive whole numbers in small tables.)  So we				�������hashcode�ķֲ��Լ��ܲ����ˣ������Ƿ�������ײҲ��0(logN)��treeȥ���ˣ��������һ�£�
     * apply a transform that spreads the impact of higher bits					��С��ϵͳ�Ŀ���Ҳ���������Ϊ��λû�в����±�ļ��㣨table�ĳ��ȹ�С�����������ײ
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     */
    static final int hash(Object key) {											//key��hashֵ��16λ���䣬��16λ�ڸ�16λ�����Ϊkey������hashֵ
        int h;																	h:         1111 1111 1111 1111 1000 0100 0010 0001
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);			h>>>16     0000 0000 0000 0000 1111 1111 1111 1111
    }																			h^(h>>>16) 1111 1111 1111 1111 0111 1011 1101 1110
																				���Ľ������Ǻ�16λ

    /**
     * Returns x's Class if it is of the form "class C implements				//���xʵ����comparable�ӿڣ��򷵻�c�࣬���򷵻�null
     * Comparable<C>", else null.
     */
    static Class<?> comparableClassFor(Object x) {								//346-373�� �ò���������ȥ����
        if (x instanceof Comparable) {
            Class<?> c; Type[] ts, as; Type t; ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                        ((p = (ParameterizedType)t).getRawType() ==
                         Comparable.class) &&
                        (as = p.getActualTypeArguments()) != null &&
                        as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable
     * class), else 0.
     */
    @SuppressWarnings({"rawtypes","unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable)k).compareTo(x));
    }

    /**
     * Returns a power of two size for the given target capacity.
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /* ---------------- Fields -------------- */

    /**
     * The table, initialized on first use, and resized as								//table�ڵ�һ��ʹ��ʱ����ʼ�������ҶԴ�С���б�Ҫ�ĵ���������һ������Ϊ2
     * necessary. When allocated, length is always a power of two.							�������ݵĴ�С������Ҳ�����г���Ϊ0���������Ŀǰ������Ҫ��
     * (We also tolerate length zero in some operations to allow
     * bootstrapping mechanics that are currently not needed.)							//hashmap��Ͱ�����û�й�ϣ��ײ��hashmap����һ�����飬����Ĳ�ѯʱ�临�Ӷ���0(1)
     */																					����hashmap����ʱ�临�Ӷ�Ϊ0(1)��������е����ݶ���ͬһ��С��λ�ã���n��������ɵ�	
    transient Node<K,V>[] table;														����ʱ�临�Ӷ�Ϊ0(n)������hashmap�����ʱ�临�Ӷ�Ϊ0(n)���������ﵽ8��Ԫ��ʱ
																						�ع�Ϊ���������������Ĳ�ѯʱ�临�Ӷ�Ϊ0(logN)������1.8��hashmap��ʱ�临�Ӷ�Ϊ0(logN)
    /**																					
     * Holds cached entrySet(). Note that AbstractMap fields are used					//hashmap������ֵ��Ϊ�õ�set������hashmap�в����ܴ���key��valueͬʱ��ͬ�����
     * for keySet() and values().
     */
    transient Set<Map.Entry<K,V>> entrySet;

    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

    /**
     * The number of times this HashMap has been structurally modified					//�����������׵���fail-fast��modcountָ������fail-fast������hashmap�ṹ�����仯�Ĵ���
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     */
    transient int modCount;

    /**
     * The next size value at which to resize (capacity * load factor).
     *
     * @serial
     */
    // (The javadoc description is true upon serialization.
    // Additionally, if the table array has not been allocated, this
    // field holds the initial array capacity, or zero signifying
    // DEFAULT_INITIAL_CAPACITY.)
    int threshold;																		//�����ķ�ֵ

    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    final float loadFactor;																//��������

    /* ---------------- Public operations -------------- */

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial					//����һ���յ�hashmap����ָ���ĳ�ʼ��������16���ͼ�������
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public HashMap(int initialCapacity, float loadFactor) {								//����hashmap���������ڳ�ʼ��������������ӣ�����쳣���׳��Ƿ������쳣
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial					//����һ��ӵ�г�ʼ��������Ĭ�ϼ������ӣ�0.75����hashmap
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity			//����һ��ӵ�г�ʼ��������16����Ĭ�ϼ������ӣ�0.75����hashmap
     * (16) and the default load factor (0.75).
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the					//����һ��ָ��map�г�ʼ��������Ĭ�ϼ������ӣ�0.75����hashmap
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     *
     * @param   m the map whose mappings are to be placed in this map
     * @throws  NullPointerException if the specified map is null
     */
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

    /**
     * Implements Map.putAll and Map constructor										//����map�Ľṹ������map������ȫ����ֵ
     *																						evict��ʼ��hashmapʱ��false����������Ϊtrue
     * @param m the map
     * @param evict false when initially constructing this map, else
     * true (relayed to method afterNodeInsertion).
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {			//��һ��map����ʼ���������ж������Ƿ�Ҫ��չ
        int s = m.size();																
        if (s > 0) {
            if (table == null) { // pre-size
                float ft = ((float)s / loadFactor) + 1.0F;
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                         (int)ft : MAXIMUM_CAPACITY);
                if (t > threshold)
                    threshold = tableSizeFor(t);										//threshold����ǰ���������С�������ٰ�Ԫ�ظ���һ��
            }
            else if (s > threshold)
                resize();
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /**																					//520-536��
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**																					//����ָ��keyֵ��valueֵ������˼�û��ӳ���򷵻�null
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key						//����Ƕ�����ķ������
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>					//ӳ��Ϊ����û��ӳ�������ֲ�ͬ�������Ӧ���������ֲ�ͬ�������key��value������Ϊnull
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     */
    public V get(Object key) {															//����keyֵ��ȡ��Ӧ��valueֵ�����������get()����
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * Implements Map.get and related methods											//ʵ��get()�������漰���ķ���
     *
     * @param hash hash for key
     * @param key the key
     * @return the node, or null if none
     */
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node						//��Ͱ�ĵ�һ����ֵ�Լ��һ���Ƿ�Ϊ��һ��Ԫ�ء���λ��һ�����ж��Ƿ�ΪTreeNode
                ((k = first.key) == key || (key != null && key.equals(k))))					�ǵĻ�����getTreeNode()������ѭ������ÿ����ֵ��
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    /**																	
     * Returns <tt>true</tt> if this map contains a mapping for the						//�ж�map�Ƿ���key�ļ�
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * Associates the specified value with the specified key in this map.				//ԭ�е�key��valueһһ��Ӧ�����µ�ӳ��ֵvalue��������ɵ�value
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * Implements Map.put and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)								//���tableΪ�գ����ʼ��
            n = (tab = resize()).length;					
        if ((p = tab[i = (n - 1) & hash]) == null)										//��ϣֵ����������õ�Ҫ�����λ�ã������ͬkey�ģ�h-1��&hash��ͬ����ôҪ�洢
            tab[i] = newNode(hash, key, value, null);									��ͬһ�������±�λ�ã��������ͽй�ϣ��ײ��������±�ûֵ����洢�����±�λ��
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))					//�����ϣֵ��ͬ������key��ͬ�������key��valueֵ
                e = p;
            else if (p instanceof TreeNode)												//����±�������TreeNode���ͣ�����������ӵ��������
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {									//����Ҫ��ӵ��Ǹ���ֵ��λ��
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);						//���µ�Node��ӵ�����β
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st			//�����������Ľڵ�����ﵽ8ʱ���������޸�Ϊ������ṹ
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key								//�����м�λkey�ļ�ֵ�ԣ�������ֵvalue
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }

    /**
     * Initializes or doubles table size.  If null, allocates in						//��ʼ�������������Ϊ2������ֵΪ��ʱ������ݳ�ʼ�������ٿռ����������顣
     * accord with initial capacity target held in field threshold.						������Ϊ����ʹ��2���ݶ�������Ĵ�С������Ҫô����ԭ�����±꣬�����ƶ���
     * Otherwise, because we are using power-of-two expansion, the						������ĸ�λ�±꣬����������ʼ��������16����������2�����ݴ洢���±�Ϊ1��λ
     * elements from each bin must either stay at same index, or move					�ã����ݺ���2�����ݿ��Դ����±�Ϊ1����Ϊ16+1��λ���ϣ�
     * with a power of two offset in the new table.
     *
     * @return the table
     */
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;											//����������������������ֵ����ΪInteger.MAX_VALUE
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold								//��ֵ����Ϊԭ��2�� 
        }
        else if (oldThr > 0) // initial capacity was placed in threshold				//��ֵ����0�����µ�����newCap��Ϊ��ֵ
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;											//��ʼ��ʱΪĬ�� 
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {																//�·�ֵΪ0������Ҫ�����µķ�ֵ
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;																//�����µķ�ֵ
        @SuppressWarnings({"rawtypes","unchecked"})										//�����µ�Ͱ
            Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {											//���ɵ�Ͱ��Ϊ�գ�������������������
                    oldTab[j] = null;													//�����Ͱֻ��һ��Ԫ�أ���ֱ�Ӹ����µ�Ͱ����
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)										//����Ǻ����������ú�����Լ��ķ�װ����
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);			
                    else { // preserve order											 //Ͱ���ж��Ԫ�أ��������������ӵ���Ӧ��λ��
                        Node<K,V> loHead = null, loTail = null;	
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {								//������ԭ����Ͱ�ļ�ֵ��
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {														//����ԭ��Ͱ�ļ�ֵ�� 
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;											//����ԭ����Ͱ 
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;								//���ڷ�ԭ����Ͱ  
                        }
                    }
                }
            }
        }
        return newTab;
    }

    /**
     * Replaces all linked nodes in bin at index for given hash unless                //�ڸ�����ϣֵ���ú�������滻��������ڵ㣬���ǹ�ϣ��̫С����Ҫ����
     * table is too small, in which case resizes instead.
     */
    final void treeifyBin(Node<K,V>[] tab, int hash) {								  //775--787 ����һ��ָ����map����һ��map��
        int n, index; Node<K,V> e;
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)					 //��Ͱ�������� MIN_TREEIFY_CAPACITY ��64����ʱ�Ž������Ϊ�����
            resize();																//Ͱ̫����resize()
        else if ((e = tab[index = (n - 1) & hash]) != null) {						//�����ӵĽ��תΪ�������ṹ 
            TreeNode<K,V> hd = null, tl = null;
            do {
                TreeNode<K,V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);													//������ṹתΪ������ 
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        putMapEntries(m, true);
    }

    /**
     * Removes the mapping for the specified key from this map if present.				//�ӵ�ǰmap���Ƴ���ָ��map�е�keyֵ
     *
     * @param  key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V remove(Object key) {														//�Ƴ�����ֵ�� 
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
    }

    /**
     * Implements Map.remove and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to match if matchValue, else ignored
     * @param matchValue if true only remove if value is equal
     * @param movable if false do not move other nodes while removing
     * @return the node, or null if none
     */
    final Node<K,V> removeNode(int hash, Object key, Object value,
                               boolean matchValue, boolean movable) {
        Node<K,V>[] tab; Node<K,V> p; int n, index;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (p = tab[index = (n - 1) & hash]) != null) {								
            Node<K,V> node = null, e; K k; V v;											//�ҵ���Ӧ��Ͱ 
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;																//����Ͱ��һ����ֵ�� 
            else if ((e = p.next) != null) {
                if (p instanceof TreeNode)												//��Ҫ����Ͱ����ļ�ֵ��
                    node = ((TreeNode<K,V>)p).getTreeNode(hash, key);					//���ں�����ṹ
                else {																	//��������ṹ  
                    do {																 //������������
                        if (e.hash == hash &&
                            ((k = e.key) == key ||
                             (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            if (node != null && (!matchValue || (v = node.value) == value ||
                                 (value != null && value.equals(v)))) {					//�ҵ���Ҫremove�ļ�ֵ��
                if (node instanceof TreeNode)											//�ײ�ṹΪ����� 
                    ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                else if (node == p)														//Ҫremove�ļ�ֵ��ΪͰ��һ��Ԫ��
                    tab[index] = node.next;
                else
                    p.next = node.next;
                ++modCount;
                --size;
                afterNodeRemoval(node);
                return node;
            }
        }
        return null;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {																//�������ֵ��  
        Node<K,V>[] tab;
        modCount++;
        if ((tab = table) != null && size > 0) {
            size = 0;																	//��Ͱ��Ϊnull,Ͱ�е�����Ҳ�ͳ�Ϊ�����ˣ���Ϊ���ݿɴ��Է����������Ѿ��ǲ��ɴ��� 
            for (int i = 0; i < tab.length; ++i)
                tab[i] = null;
        }
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the					//���ָ����value����һ�����߶��keyֵ��֮��Ӧ�򷵻�true
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    public boolean containsValue(Object value) {										//�ж��Ƿ����ֵvalue  
        Node<K,V>[] tab; V v;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {										 //����ÿ��Ͱ 
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {						//����Ͱ�е�ÿһ����ֵ��
                    if ((v = e.value) == value ||
                        (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.					//����map�е�keyֵ��set��map����Ӱ�죬���map���޸ģ�����set�ڵ���������������
     * The set is backed by the map, so changes to the map are								��remove��������������Ͳ��ᱻ���壬set֧��Ԫ�ر�ɾ��������ɾ��ԭ��ӳ���ֵ
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<K> keySet() {															//�������м���ɵ�Set
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    final class KeySet extends AbstractSet<K> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<K> iterator()     { return new KeyIterator(); }
        public final boolean contains(Object o) { return containsKey(o); }
        public final boolean remove(Object key) {
            return removeNode(hash(key), key, null, false, true) != null;
        }
        public final Spliterator<K> spliterator() {
            return new KeySpliterator<>(HashMap.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super K> action) {
            Node<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node<K,V> e = tab[i]; e != null; e = e.next)
                        action.accept(e.key);											//������ÿһ��key��������action��
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.			//������ǽ�key���set�������ǽ�value����collection��
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a view of the values contained in this map
     */
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    final class Values extends AbstractCollection<V> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<V> iterator()     { return new ValueIterator(); }
        public final boolean contains(Object o) { return containsValue(o); }
        public final Spliterator<V> spliterator() {
            return new ValueSpliterator<>(HashMap.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super V> action) {
            Node<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node<K,V> e = tab[i]; e != null; e = e.next)
                        action.accept(e.value);
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.					//�����ǽ���ֵ�����entrySet
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            Object key = e.getKey();
            Node<K,V> candidate = getNode(hash(key), key);
            return candidate != null && candidate.equals(e);
        }
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object key = e.getKey();
                Object value = e.getValue();
                return removeNode(hash(key), key, value, true, true) != null;
            }
            return false;
        }
        public final Spliterator<Map.Entry<K,V>> spliterator() {
            return new EntrySpliterator<>(HashMap.this, 0, -1, 0, 0);
        }
        public final void forEach(Consumer<? super Map.Entry<K,V>> action) {
            Node<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = table) != null) {
                int mc = modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node<K,V> e = tab[i]; e != null; e = e.next)
                        action.accept(e);
                }
                if (modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }
    }

    // Overrides of JDK8 Map extension methods

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? defaultValue : e.value;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return putVal(hash(key), key, value, true, true);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return removeNode(hash(key), key, value, true, true) != null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Node<K,V> e; V v;
        if ((e = getNode(hash(key), key)) != null &&
            ((v = e.value) == oldValue || (v != null && v.equals(oldValue)))) {
            e.value = newValue;
            afterNodeAccess(e);
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        Node<K,V> e;
        if ((e = getNode(hash(key), key)) != null) {
            V oldValue = e.value;
            e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
        return null;
    }

    @Override
    public V computeIfAbsent(K key,	
                             Function<? super K, ? extends V> mappingFunction) {			//����java8�ı��ػ��淽ʽ �������MAP�в�����ָ��key��ֵ�����Զ�����mappingFunction(key)
        if (mappingFunction == null)														����key��value,Ȼ��key = value���뵽����Map,java8��ʹ��thread-safe�ķ�ʽ��cache�д�ȡ��¼
            throw new NullPointerException();
        int hash = hash(key);
        Node<K,V>[] tab; Node<K,V> first; int n, i;
        int binCount = 0;
        TreeNode<K,V> t = null;
        Node<K,V> old = null;
        if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
            n = (tab = resize()).length;													//��Ҫ���� 
        if ((first = tab[i = (n - 1) & hash]) != null) {
            if (first instanceof TreeNode)													//�ײ��Ǻ����
                old = (t = (TreeNode<K,V>)first).getTreeNode(hash, key);
            else {																			//�ײ������� 
                Node<K,V> e = first; K k;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        old = e;
                        break;
                    }
                    ++binCount;
                } while ((e = e.next) != null);
            }
            V oldValue;
            if (old != null && (oldValue = old.value) != null) {
                afterNodeAccess(old);
                return oldValue;
            }
        }
        V v = mappingFunction.apply(key);													//���û�и�key�ļ�ֵ�� 
        if (v == null) {
            return null;
        } else if (old != null) {
            old.value = v;
            afterNodeAccess(old);
            return v;
        }
        else if (t != null)																	//��Ӻ������� 
            t.putTreeVal(this, tab, hash, key, v);
        else {																				//���������
            tab[i] = newNode(hash, key, v, first);
            if (binCount >= TREEIFY_THRESHOLD - 1)
                treeifyBin(tab, hash);
        }
        ++modCount;
        ++size;
        afterNodeInsertion(true);
        return v;
    }

    public V computeIfPresent(K key,	
                              BiFunction<? super K, ? super V, ? extends V> remappingFunction) {	
        if (remappingFunction == null)														//���ø�computeIfAbsent()�෴������ֻ���ڵ�ǰMap�д���keyֵ��ӳ���ҷ�nullʱ��
            throw new NullPointerException();												�ŵ���remappingFunction�����remappingFunctionִ�н��Ϊnull����ɾ��key��ӳ�䣬
        Node<K,V> e; V oldValue;															����ʹ�øý���滻keyԭ����ӳ�䣮
        int hash = hash(key);
        if ((e = getNode(hash, key)) != null &&
            (oldValue = e.value) != null) {
            V v = remappingFunction.apply(key, oldValue);
            if (v != null) {
                e.value = v;
                afterNodeAccess(e);
                return v;
            }
            else
                removeNode(hash, key, null, false, true);
        }
        return null;
    }

    @Override
    public V compute(K key,																	//�����ǰ�remappingFunction�ļ�����������key�ϣ����������Ϊnull��
                     BiFunction<? super K, ? super V, ? extends V> remappingFunction) {			����Map��ɾ��key��ӳ�䣮
        if (remappingFunction == null)
            throw new NullPointerException();
        int hash = hash(key);
        Node<K,V>[] tab; Node<K,V> first; int n, i;
        int binCount = 0;
        TreeNode<K,V> t = null;
        Node<K,V> old = null;
        if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((first = tab[i = (n - 1) & hash]) != null) {
            if (first instanceof TreeNode)
                old = (t = (TreeNode<K,V>)first).getTreeNode(hash, key);
            else {
                Node<K,V> e = first; K k;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        old = e;
                        break;
                    }
                    ++binCount;
                } while ((e = e.next) != null);
            }
        }
        V oldValue = (old == null) ? null : old.value;
        V v = remappingFunction.apply(key, oldValue);
        if (old != null) {
            if (v != null) {
                old.value = v;
                afterNodeAccess(old);
            }
            else
                removeNode(hash, key, null, false, true);
        }
        else if (v != null) {
            if (t != null)
                t.putTreeVal(this, tab, hash, key, v);
            else {
                tab[i] = newNode(hash, key, v, first);
                if (binCount >= TREEIFY_THRESHOLD - 1)
                    treeifyBin(tab, hash);
            }
            ++modCount;
            ++size;
            afterNodeInsertion(true);
        }
        return v;
    }

    @Override
    public V merge(K key, V value,														//���Map��key��Ӧ��ӳ�䲻���ڻ���Ϊnull����value��������null��������key�ϣ�
                   BiFunction<? super V, ? super V, ? extends V> remappingFunction) {		����ִ��remappingFunction�����ִ�н����null���øý����key������������Map
        if (value == null)																	��ɾ��key��ӳ�䣮
            throw new NullPointerException();
        if (remappingFunction == null)
            throw new NullPointerException();
        int hash = hash(key);
        Node<K,V>[] tab; Node<K,V> first; int n, i;
        int binCount = 0;
        TreeNode<K,V> t = null;
        Node<K,V> old = null;
        if (size > threshold || (tab = table) == null ||
            (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((first = tab[i = (n - 1) & hash]) != null) {
            if (first instanceof TreeNode)
                old = (t = (TreeNode<K,V>)first).getTreeNode(hash, key);
            else {
                Node<K,V> e = first; K k;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        old = e;
                        break;
                    }
                    ++binCount;
                } while ((e = e.next) != null);
            }
        }
        if (old != null) {
            V v;
            if (old.value != null)
                v = remappingFunction.apply(old.value, value);
            else
                v = value;
            if (v != null) {
                old.value = v;
                afterNodeAccess(old);
            }
            else
                removeNode(hash, key, null, false, true);
            return v;
        }
        if (value != null) {
            if (t != null)
                t.putTreeVal(this, tab, hash, key, value);
            else {
                tab[i] = newNode(hash, key, value, first);
                if (binCount >= TREEIFY_THRESHOLD - 1)
                    treeifyBin(tab, hash);
            }
            ++modCount;
            ++size;
            afterNodeInsertion(true);
        }
        return value;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {					//�����Ƕ�Map�е�ÿ��ӳ��ִ��actionָ���Ĳ���������BiConsumer��һ�������ӿڣ�
        Node<K,V>[] tab;															������һ����ʵ�ַ���void accept(T t, U u)��BinConsumer�ӿ����ֺ�accept()����
        if (action == null)															���ֶ�����Ҫ���벻Ҫ�������ǡ�
            throw new NullPointerException();
        if (size > 0 && (tab = table) != null) {
            int mc = modCount;
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next)
                    action.accept(e.key, e.value);
            }
            if (modCount != mc)
                throw new ConcurrentModificationException();
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {	//�����Ƕ�Map�е�ÿ��ӳ��ִ��functionָ���Ĳ���������function��ִ�н���滻
        Node<K,V>[] tab;																	ԭ����value������BiFunction��һ�������ӿڣ�������һ����ʵ�ַ���R apply(T t, U u)��
        if (function == null)																��Ҫ����˶�ĺ����ӿ��ŵ�����Ϊʹ�õ�ʱ���������Ҫ֪�����ǵ����֣�
            throw new NullPointerException();
        if (size > 0 && (tab = table) != null) {
            int mc = modCount;
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    e.value = function.apply(e.key, e.value);
                }
            }
            if (modCount != mc)
                throw new ConcurrentModificationException();
        }
    }

    /* ------------------------------------------------------------ */
    // Cloning and serialization														//��¡�����л�

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and			//hashmap��ǳ������keys��value��û�б�����
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {																//clone�����������µ�hashmap��������table����Ҳ�������ɵģ�������������ݻ���һ���ģ�Ԫ�أ�										
        HashMap<K,V> result;															clone�����ã�Ҳ�͵��¸ı����õ�ֵ��ԭ����Ԫ��Ҳ�ᷢ���仯��������ɾ�򲻻ᷢ���仯
        try {
            result = (HashMap<K,V>)super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
        result.reinitialize();
        result.putMapEntries(this, false);
        return result;
    }

    // These methods are also used when serializing HashSets							//��Щ��������HashSet�����л�����
    final float loadFactor() { return loadFactor; }
    final int capacity() {
        return (table != null) ? table.length :
            (threshold > 0) ? threshold :
            DEFAULT_INITIAL_CAPACITY;
    }

    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream (i.e.,				//���л���ԭ��
     * serialize it).
     *
     * @serialData The <i>capacity</i> of the HashMap (the length of the
     *             bucket array) is emitted (int), followed by the
     *             <i>size</i> (an int, the number of key-value
     *             mappings), followed by the key (Object) and value (Object)
     *             for each key-value mapping.  The key-value mappings are
     *             emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException {
        int buckets = capacity();
        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();
        s.writeInt(buckets);
        s.writeInt(size);
        internalWriteEntries(s);
    }

    /**	
     * Reconstitute the {@code HashMap} instance from a stream (i.e.,					//�����л���ԭ��
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        // Read in the threshold (ignored), loadfactor, and any hidden stuff
        s.defaultReadObject();
        reinitialize();
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new InvalidObjectException("Illegal load factor: " +
                                             loadFactor);
        s.readInt();                // Read and ignore number of buckets
        int mappings = s.readInt(); // Read number of mappings (size)
        if (mappings < 0)
            throw new InvalidObjectException("Illegal mappings count: " +
                                             mappings);
        else if (mappings > 0) { // (if zero, use defaults)
            // Size the table using given load factor only if within
            // range of 0.25...4.0
            float lf = Math.min(Math.max(0.25f, loadFactor), 4.0f);
            float fc = (float)mappings / lf + 1.0f;
            int cap = ((fc < DEFAULT_INITIAL_CAPACITY) ?
                       DEFAULT_INITIAL_CAPACITY :
                       (fc >= MAXIMUM_CAPACITY) ?
                       MAXIMUM_CAPACITY :
                       tableSizeFor((int)fc));
            float ft = (float)cap * lf;
            threshold = ((cap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY) ?
                         (int)ft : Integer.MAX_VALUE);
            @SuppressWarnings({"rawtypes","unchecked"})
                Node<K,V>[] tab = (Node<K,V>[])new Node[cap];
            table = tab;

            // Read the keys and values, and put the mappings in the HashMap
            for (int i = 0; i < mappings; i++) {
                @SuppressWarnings("unchecked")
                    K key = (K) s.readObject();
                @SuppressWarnings("unchecked")
                    V value = (V) s.readObject();
                putVal(hash(key), key, value, false, false);
            }
        }
    }

    /* ------------------------------------------------------------ */				//Spliterator��splitable iterator�ɷָ���������ӿ���JavaΪ�˲��б�������Դ�е�Ԫ��
    // iterators																	����Ƶĵ���������������������Java�ṩ��˳�����������Iterator����һ����˳�������һ���ǲ��б���
																					������Java�ṩ˳�����������Iteratorʱ���Ǹ�ʱ���ǵ���ʱ���������ڶ��ʱ���£�
    abstract class HashIterator {													˳������Ѿ���������������...��ΰѶ��������䵽��ͬ���ϲ���ִ�У���������󷢻Ӷ�˵�������
        Node<K,V> next;        // next entry to return								����SpliteratorӦ�˶�����
        Node<K,V> current;     // current entry										��Ϊ��������Դ����...�������������������������Java�Ѿ�Ĭ���ڼ��Ͽ����Ϊ���е����ݽṹ�ṩ��
        int expectedModCount;  // for fast-fail										һ��Ĭ�ϵ�Spliteratorʵ�֣���Ӧ�����ʵ����ʵ���ǵײ�Stream��β��б�����Stream.isParallel()����ʵ������
        int index;             // current slot										���ƽ���õ�Spliterator������ǲ����...��ΪJava8�������һ�����ú���ʽ��̵�˼�룬��ֻ��Ҫ����JDK��Ҫ
																					��ʲô�������񣬹�עҵ����������β��У���ô����Ч����ߣ��ͽ���JDK�Լ�ȥ˼�����Ż��ٶ���
        HashIterator() {															��������ǰд��β����Ĵ��뱻֧��Ŀ־�ɣ���Ϊ����������ֻ��Ҫȥ����һЩfilter��map��collect��ҵ���������
            expectedModCount = modCount;
            Node<K,V>[] t = table;
            current = next = null;
            index = 0;
            if (t != null && size > 0) { // advance to first entry
                do {} while (index < t.length && (next = t[index++]) == null);
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Node<K,V> nextNode() {
            Node<K,V>[] t;
            Node<K,V> e = next;
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (e == null)
                throw new NoSuchElementException();
            if ((next = (current = e).next) == null && (t = table) != null) {
                do {} while (index < t.length && (next = t[index++]) == null);
            }
            return e;
        }

        public final void remove() {
            Node<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            current = null;
            K key = p.key;
            removeNode(hash(key), key, null, false, false);
            expectedModCount = modCount;
        }
    }

    final class KeyIterator extends HashIterator
        implements Iterator<K> {
        public final K next() { return nextNode().key; }
    }

    final class ValueIterator extends HashIterator
        implements Iterator<V> {
        public final V next() { return nextNode().value; }
    }

    final class EntryIterator extends HashIterator
        implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextNode(); }
    }

    /* ------------------------------------------------------------ */
    // spliterators

    static class HashMapSpliterator<K,V> {
        final HashMap<K,V> map;
        Node<K,V> current;          // current node
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate
        int expectedModCount;       // for comodification checks

        HashMapSpliterator(HashMap<K,V> m, int origin,
                           int fence, int est,
                           int expectedModCount) {
            this.map = m;
            this.index = origin;
            this.fence = fence;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getFence() { // initialize fence and size on first use
            int hi;
            if ((hi = fence) < 0) {
                HashMap<K,V> m = map;
                est = m.size;
                expectedModCount = m.modCount;
                Node<K,V>[] tab = m.table;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            return hi;
        }

        public final long estimateSize() {											//�÷������ڹ��㻹ʣ�¶��ٸ�Ԫ����Ҫ����
            getFence(); // force init
            return (long) est;
        }
    }

    static final class KeySpliterator<K,V>
        extends HashMapSpliterator<K,V>
        implements Spliterator<K> {
        KeySpliterator(HashMap<K,V> m, int origin, int fence, int est,
                       int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        public KeySpliterator<K,V> trySplit() {                                    //trySplit�������ΪSpliteratorר����Ƶķ�������������ͨ��Iterator���÷�����ѵ�ǰ
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;					Ԫ�ػ���һ���ֳ�ȥ����һ���µ�Spliterator��Ϊ���أ�����Spliterator��Ტ��ִ�У�
            return (lo >= mid || current != null) ? null :							���Ԫ�ظ���С���޷������򷵻�null
                new KeySpliterator<>(map, lo, index = mid, est >>>= 1,
                                        expectedModCount);
        }

        public void forEachRemaining(Consumer<? super K> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashMap<K,V> m = map;
            Node<K,V>[] tab = m.table;
            if ((hi = fence) < 0) {
                mc = expectedModCount = m.modCount;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
                mc = expectedModCount;
            if (tab != null && tab.length >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Node<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p.key);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super K> action) {							//tryAdvance����˳����ÿ��Ԫ�أ�����Iterator���������Ԫ��Ҫ�����򷵻�true�����򷵻�false
            int hi;
            if (action == null)
                throw new NullPointerException();
            Node<K,V>[] tab = map.table;
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        K k = current.key;
                        current = current.next;
                        action.accept(k);
                        if (map.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {													//��ʵ���Ǳ�ʾ��Spliterator����Щ���ԣ����ڿ��Ը��ÿ��ƺ��Ż�Spliterator��ʹ�ã�
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |					
                Spliterator.DISTINCT;
        }
    }

    static final class ValueSpliterator<K,V>
        extends HashMapSpliterator<K,V>
        implements Spliterator<V> {
        ValueSpliterator(HashMap<K,V> m, int origin, int fence, int est,
                         int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        public ValueSpliterator<K,V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new ValueSpliterator<>(map, lo, index = mid, est >>>= 1,
                                          expectedModCount);
        }

        public void forEachRemaining(Consumer<? super V> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashMap<K,V> m = map;
            Node<K,V>[] tab = m.table;
            if ((hi = fence) < 0) {
                mc = expectedModCount = m.modCount;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
                mc = expectedModCount;
            if (tab != null && tab.length >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Node<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p.value);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Node<K,V>[] tab = map.table;
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        V v = current.value;
                        current = current.next;
                        action.accept(v);
                        if (map.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0);
        }
    }

    static final class EntrySpliterator<K,V>
        extends HashMapSpliterator<K,V>
        implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(HashMap<K,V> m, int origin, int fence, int est,
                         int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        public EntrySpliterator<K,V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                new EntrySpliterator<>(map, lo, index = mid, est >>>= 1,
                                          expectedModCount);
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K,V>> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashMap<K,V> m = map;
            Node<K,V>[] tab = m.table;
            if ((hi = fence) < 0) {
                mc = expectedModCount = m.modCount;
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
                mc = expectedModCount;
            if (tab != null && tab.length >= hi &&
                (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Node<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc)
                    throw new ConcurrentModificationException();
            }
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K,V>> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Node<K,V>[] tab = map.table;
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        Node<K,V> e = current;
                        current = current.next;
                        action.accept(e);
                        if (map.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT;
        }
    }

    /* ------------------------------------------------------------ */
    // LinkedHashMap support


    /*
     * The following package-protected methods are designed to be							//����ķ�����LinkedHashMap��д�ˣ����Ǳ������࣬�������е��ڲ���������package-protected
     * overridden by LinkedHashMap, but not by any other subclass.								���͵ģ�����Ҳ��final���Σ�������LinkedHashMap����ͼ�࣬HashSet
     * Nearly all other internal methods are also package-protected
     * but are declared final, so can be used by LinkedHashMap, view
     * classes, and HashSet.
     */

    // Create a regular (non-tree) node
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {							//����һ��������
        return new Node<>(hash, key, value, next);
    }

    // For conversion from TreeNodes to plain nodes	
    Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {								//�滻һ������ڵ� 
        return new Node<>(p.hash, p.key, p.value, next);
    }

    // Create a tree bin node
    TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {					//����һ��������ڵ�
        return new TreeNode<>(hash, key, value, next);
    }

    // For treeifyBin
    TreeNode<K,V> replacementTreeNode(Node<K,V> p, Node<K,V> next) {						//�滻һ��������ڵ� 
        return new TreeNode<>(p.hash, p.key, p.value, next);
    }

    /**
     * Reset to initial default state.  Called by clone and readObject.
     */
    void reinitialize() {
        table = null;
        entrySet = null;
        keySet = null;
        values = null;
        modCount = 0;
        threshold = 0;
        size = 0;
    }

    // Callbacks to allow LinkedHashMap post-actions
    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node<K,V> p) { }

    // Called only from writeObject, to ensure compatible ordering.
    void internalWriteEntries(java.io.ObjectOutputStream s) throws IOException {			//ͨ�����б���ʼ����ֵ��
        Node<K,V>[] tab;
        if (size > 0 && (tab = table) != null) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K,V> e = tab[i]; e != null; e = e.next) {
                    s.writeObject(e.key);
                    s.writeObject(e.value);
                }
            }
        }
    }

    /* ------------------------------------------------------------ */
    // Tree bins

    /**
     * Entry for Tree bins. Extends LinkedHashMap.Entry (which in turn						//TreeNode<K,V> �̳���LinkedHashMap.Entry<K,V> �����
     * extends Node) so can be used as extension of either regular or						��Entry<K,V>������ּ̳���Node<K,V> ���������������Node<K,V> ������ before �� after��
     * linked node.																			����TreeNode<K,V> ��û��ʹ�ã�����LinkedHashMap �������ʹ���ˣ���ô����ֻ��HashMap�Ļ���
     */																						�Ϳ�����ΪTreeNode<K,V> �̳���Node<K,V> �����ù����LinkedHashMap.Entry<K,V> �ˡ�
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // red-black tree links										//�ڵ�ĸ��� 
        TreeNode<K,V> left;																	//�ڵ������ 
        TreeNode<K,V> right;																//�ڵ���Һ��� 
        TreeNode<K,V> prev;    // needed to unlink next upon deletion						//�ڵ��ǰһ���ڵ� 
        boolean red;																		//true��ʾ��ڵ㣬false��ʾ�ڽڵ�  
        TreeNode(int hash, K key, V val, Node<K,V> next) {
            super(hash, key, val, next);
        }

        /**
         * Returns root of tree containing this node.
         */
        final TreeNode<K,V> root() {														//��ȡ������ĸ� 
            for (TreeNode<K,V> r = this, p;;) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }

        /**
         * Ensures that the given root is the first node of its bin.						//ȷ��root��Ͱ�еĵ�һ��Ԫ�� 
         */
        static <K,V> void moveRootToFront(Node<K,V>[] tab, TreeNode<K,V> root) {			//��root�Ƶ����еĵ�һ�� 
            int n;
            if (root != null && tab != null && (n = tab.length) > 0) {						//��ȡ�±�ֵ 
                int index = (n - 1) & root.hash;
                TreeNode<K,V> first = (TreeNode<K,V>)tab[index];
                if (root != first) {														//root����Ͱ�е�һ��Ԫ�� 
                    Node<K,V> rn;															//��Ͱ�еĵ�һ��Ԫ������Ϊroot
                    tab[index] = root;
                    TreeNode<K,V> rp = root.prev;											//�����rootɾ����rn.prev = rp; rp.next = rn;
                    if ((rn = root.next) != null)
                        ((TreeNode<K,V>)rn).prev = rp;
                    if (rp != null)
                        rp.next = rn;
                    if (first != null)														//��first���뵽root���� 
                        first.prev = root;
                    root.next = first;
                    root.prev = null;
                }
                assert checkInvariants(root);
            }
        }

        /**
         * Finds the node starting at root p with the given hash and key.
         * The kc argument caches comparableClassFor(key) upon first use
         * comparing keys.
         */
        final TreeNode<K,V> find(int h, Object k, Class<?> kc) {							//����hashΪh��keyΪk�Ľڵ� 
            TreeNode<K,V> p = this;
            do {
                int ph, dir; K pk;
                TreeNode<K,V> pl = p.left, pr = p.right, q;
                if ((ph = p.hash) > h)														//hС�ڽڵ��hashֵ��������ڵ�
                    p = pl;
                else if (ph < h)															//h���ڽ���hashֵ�������ҽڵ�
                    p = pr;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))					//�ҵ��򷵻� 
                    return p;
                else if (pl == null)														//��ڵ�Ϊ�գ������ҽڵ� 
                    p = pr;
                else if (pr == null)														//�ҽڵ�Ϊ�գ�������ڵ�  
                    p = pl;
                else if ((kc != null ||
                          (kc = comparableClassFor(k)) != null) &&
                         (dir = compareComparables(kc, k, pk)) != 0)
                    p = (dir < 0) ? pl : pr;
                else if ((q = pr.find(h, k, kc)) != null)									//ͨ���ҽڵ���� 
                    return q;
                else
                    p = pl;
            } while (p != null);
            return null;
        }

        /**
         * Calls find for root node.
         */
        final TreeNode<K,V> getTreeNode(int h, Object k) {									//��ȡ���ڵ㣬ͨ�����ڵ���� 
            return ((parent != null) ? root() : this).find(h, k, null);
        }

        /**
         * Tie-breaking utility for ordering insertions when equal							//Tie-breaking�������Ƚ����������hashcodeֵ�����ǲ���Ҫ���׹���ֻ��Ҫ�Ƚ������Ƿ�Ե�
         * hashCodes and non-comparable. We don't require a total								Tie-breaking���˱ȽϷ���
         * order, just a consistent insertion rule to maintain
         * equivalence across rebalancings. Tie-breaking further than
         * necessary simplifies testing a bit.
         */
        static int tieBreakOrder(Object a, Object b) {										//����������Ȼ�Ƚ�������������������������ٵ��� System.identityHashCode �������бȽϡ�
            int d;
            if (a == null || b == null ||
                (d = a.getClass().getName().
                 compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                     -1 : 1);
            return d;
        }

        /**
         * Forms tree of the nodes linked from this node.									//������תΪ������ 
         * @return root of tree
         */
        final void treeify(Node<K,V>[] tab) {
            TreeNode<K,V> root = null;
            for (TreeNode<K,V> x = this, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if (root == null) {
                    x.parent = null;														//���ڵ�����Ϊ��ɫ
                    x.red = false;
                    root = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (TreeNode<K,V> p = root;;) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                  (kc = comparableClassFor(k)) == null) ||
                                 (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);

                        TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(tab, root);
        }

        /**
         * Returns a list of non-TreeNodes replacing those linked from						//��������תΪ���� 
         * this node.
         */
        final Node<K,V> untreeify(HashMap<K,V> map) {
            Node<K,V> hd = null, tl = null;
            for (Node<K,V> q = this; q != null; q = q.next) {
                Node<K,V> p = map.replacementNode(q, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
        }

        /**
         * Tree version of putVal.															//���һ����ֵ�� 
         */
        final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                                       int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            TreeNode<K,V> root = (parent != null) ? root() : this;
            for (TreeNode<K,V> p = root;;) {
                int dir, ph; K pk;
                if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))					//��ֵ��Ϊroot���򷵻� 
                    return p;
                else if ((kc == null &&
                          (kc = comparableClassFor(k)) == null) ||
                         (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {														//ֻ���е�һ�Σ�����Ƿ��Դ��ڸü�ֵ�� 
                        TreeNode<K,V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                             (q = ch.find(h, k, kc)) != null) ||
                            ((ch = p.right) != null &&
                             (q = ch.find(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                TreeNode<K,V> xp = p;	
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    Node<K,V> xpn = xp.next;
                    TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);						//����ڵ� 
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    xp.next = x;
                    x.parent = x.prev = xp;
                    if (xpn != null)
                        ((TreeNode<K,V>)xpn).prev = x;
                    moveRootToFront(tab, balanceInsertion(root, x));						//���ƽ��
                    return null;
                }
            }
        }

        /**
         * Removes the given node, that must be present before this call.			//�Ƴ��Ѹ����Ľڵ㣬�ýڵ��ڵ���ǰ�Ǵ��ڵģ����Ǳȵ��͵ĺ��ɫɾ�����룬��Ϊ���ǲ��ܽ����ڲ��ڵ������
         * This is messier than typical red-black deletion code because we			�����������nextָ���ڱ�������ʱ��ָ������ݣ��������ǿ��Խ���tree linkages,������Ľڵ����
         * cannot swap the contents of an interior node with a leaf					����������ת������ͨ��������⶯����2-6���ڵ�ʱ��������Ҫ���������Ľṹ��
         * successor that is pinned by "next" pointers that are accessible
         * independently during traversal. So instead we swap the tree
         * linkages. If the current tree appears to have too few nodes,
         * the bin is converted back to a plain bin. (The test triggers
         * somewhere between 2 and 6 nodes, depending on tree structure).
         */
        final void removeTreeNode(HashMap<K,V> map, Node<K,V>[] tab,
                                  boolean movable) {
            int n;
            if (tab == null || (n = tab.length) == 0)
                return;
            int index = (n - 1) & hash;
            TreeNode<K,V> first = (TreeNode<K,V>)tab[index], root = first, rl;
            TreeNode<K,V> succ = (TreeNode<K,V>)next, pred = prev;
            if (pred == null)
                tab[index] = first = succ;
            else
                pred.next = succ;
            if (succ != null)
                succ.prev = pred;
            if (first == null)
                return;
            if (root.parent != null)
                root = root.root();
            if (root == null || root.right == null ||
                (rl = root.left) == null || rl.left == null) {
                tab[index] = first.untreeify(map);  // too small							//̫�پ�תΪ���� 
                return;
            }
            TreeNode<K,V> p = this, pl = left, pr = right, replacement;
            if (pl != null && pr != null) {
                TreeNode<K,V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                boolean c = s.red; s.red = p.red; p.red = c; // swap colors					//������ɫ
                TreeNode<K,V> sr = s.right;
                TreeNode<K,V> pp = p.parent;
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                }
                else {
                    TreeNode<K,V> sp = s.parent;
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    root = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                if (sr != null)
                    replacement = sr;
                else
                    replacement = p;
            }
            else if (pl != null)
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {
                TreeNode<K,V> pp = replacement.parent = p.parent;
                if (pp == null)
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }

            TreeNode<K,V> r = p.red ? root : balanceDeletion(root, replacement);

            if (replacement == p) {  // detach
                TreeNode<K,V> pp = p.parent;
                p.parent = null;
                if (pp != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                }
            }
            if (movable)
                moveRootToFront(tab, r);
        }

        /**
         * Splits nodes in a tree bin into lower and upper tree bins,							//��һ���������ָ�ɵ͸������������Ƿ����ڵ㣨���ԭ�������ڵ��С��
         * or untreeifies if now too small. Called only from resize;							�������ֻ��resizeʱ�����ã����������ǹ��ڷָ�����������
         * see above discussion about split bits and indices.
         *
         * @param map the map
         * @param tab the table for recording bin heads
         * @param index the index of the table being split
         * @param bit the bit of hash to split on
         */
        final void split(HashMap<K,V> map, Node<K,V>[] tab, int index, int bit) {				//�����̫���Ͱ�ָ�  
            TreeNode<K,V> b = this;
            // Relink into lo and hi lists, preserving order
            TreeNode<K,V> loHead = null, loTail = null;
            TreeNode<K,V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;
            for (TreeNode<K,V> e = b, next; e != null; e = next) {
                next = (TreeNode<K,V>)e.next;
                e.next = null;
                if ((e.hash & bit) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                }
                else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);											//̫С��תΪ����  
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
        }

        /* ------------------------------------------------------------ */
        // Red-black tree methods, all adapted from CLR											//�����������ȫ����CLR�ı�

        static <K,V> TreeNode<K,V> rotateLeft(TreeNode<K,V> root,								//����ת
                                              TreeNode<K,V> p) {
            TreeNode<K,V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        static <K,V> TreeNode<K,V> rotateRight(TreeNode<K,V> root,								//����ת 
                                               TreeNode<K,V> p) {
            TreeNode<K,V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        static <K,V> TreeNode<K,V> balanceInsertion(TreeNode<K,V> root,							//��֤�����ƽ�� 
                                                    TreeNode<K,V> x) {
            x.red = true;
            for (TreeNode<K,V> xp, xpp, xppl, xppr;;) {
                if ((xp = x.parent) == null) {
                    x.red = false;																//���� ����1��������Ǹ��ڵ㣬����ɫ��Ϊ��ɫ���������ô��� 
                    return x;
                }
                else if (!xp.red || (xpp = xp.parent) == null)
                    return root;																//���� ����2������Ľڵ�ĸ��ڵ��Ǻ�ɫ�����ô��� 														
                if (xp == (xppl = xpp.left)) {													//�ԳƵģ��ò����ǲ��뵽��ߵ���
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xppr.red = false;														//���� ����3�������ڵ����ڵ���Ϊ��ɫ�����游��Ϊ��ɫ
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.right) {													//���� ����4����Ҫ����תһ�� 
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {														//���� ����5����Ҫ����һ�� 
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                }
                else {																			//�ԳƵģ��ò����ǲ��뵽�ұߵ���
                    if (xppl != null && xppl.red) {
                        xppl.red = false;														//���� ����3
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.left) {														//���� ����4  				
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {														//���� ����5
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        static <K,V> TreeNode<K,V> balanceDeletion(TreeNode<K,V> root,							//ɾ�������ƽ�� 
                                                   TreeNode<K,V> x) {
            for (TreeNode<K,V> xp, xpl, xpr;;)  {
                if (x == null || x == root)														//ɾ�� ���1�����ڵ㣬����Ҫ���� 
                    return root;
                else if ((xp = x.parent) == null) {												//ɾ�����Ǹ��ڵ㣬�����ڵ���Ϊ��ɫ
                    x.red = false;
                    return x;
                }
                else if (x.red) {
                    x.red = false;
                    return root;
                }
                else if ((xpl = xp.left) == x) {												//������� 
                    if ((xpr = xp.right) != null && xpr.red) {									//ɾ�� ���2
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                            (sl == null || !sl.red)) {	
                            xpr.red = true;														//ɾ�� ���3 
                            x = xp;
                        }
                        else {
                            if (sr == null || !sr.red) {										//ɾ�� ���5 
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                    null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;													//ɾ�� ���6
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                }
                else { // symmetric
                    if (xpl != null && xpl.red) {												//�������ǶԳƵ� 
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) &&
                            (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        }
                        else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ?
                                    null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Recursive invariant check														//����Ƿ���Ϻ���� 
         */
        static <K,V> boolean checkInvariants(TreeNode<K,V> t) {
            TreeNode<K,V> tp = t.parent, tl = t.left, tr = t.right,
                tb = t.prev, tn = (TreeNode<K,V>)t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }
    }

}
