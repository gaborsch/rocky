package rockstar.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class NativeObject {
	
	public static boolean isNativeDisabled = false;

	private static final Class<?>[] LONG_CLASSES = new Class<?>[] { long.class, Long.class };
	private static final Class<?>[] INT_CLASSES = new Class<?>[] { int.class, Integer.class };
	private static final Class<?>[] DOUBLE_CLASSES = new Class<?>[] { double.class, Double.class };
	private static final Class<?>[] CHAR_CLASSES = new Class<?>[] { char.class, Character.class };
	private static final Class<?>[] SHORT_CLASSES = new Class<?>[] { short.class, Short.class };
	private static final Class<?>[] BYTE_CLASSES = new Class<?>[] { byte.class, Byte.class };
	private static final Class<?>[] FLOAT_CLASSES = new Class<?>[] { float.class, Float.class };
	private static final Class<?>[] OTHER_NUMERIC_CLASSES = new Class<?>[] { BigDecimal.class, BigInteger.class };
	private static final Class<?>[] ARRAY_CLASSES = new Class<?>[] { List.class, Map.class };
	
	private Class<?> nativeClass;
	private boolean isStaticInstance;
	private Object nativeObject;

	private NativeObject(Class<?> nativeClass, Object nativeObject) {
		this.nativeClass = nativeClass;
		this.isStaticInstance = false;
		this.nativeObject = nativeObject;
	}

	private NativeObject(Class<?> nativeClass, Object nativeObject, boolean isStaticInstance) {
		this.nativeClass = nativeClass;
		this.isStaticInstance = isStaticInstance;
		this.nativeObject = nativeObject;
	}
	
	public static NativeObject getStatic(QualifiedClassName qcn) {
		if (isNativeDisabled) {
			return null;
		}
		
		Class<?> nativeClass = getClassForName(qcn);
		if (nativeClass == null) {
			return null;
		}
		return new NativeObject(nativeClass, null, true);
	}

	public NativeObject newInstance(List<Value> ctorParams, BlockContext ctx) {
		if (isNativeDisabled) {
			return null;
		}
		Constructor<?> ctor = getConstructor(nativeClass, ctorParams);
		Object[] initArgs = convertValuesToJava(ctor.getParameterTypes(), ctorParams, ctx);
		try {
			Object nativeObject = ctor.newInstance(initArgs);
			return new NativeObject(nativeClass, nativeObject);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RockstarRuntimeException("Cannot instantiate native class: " + nativeClass.getCanonicalName()
					+ " with params " + ctorParams);
		}
	}

	public Class<?> getNativeClass() {
		return nativeClass;
	}

	public boolean isStaticInstance() {
		return isStaticInstance;
	}

	public Object getNativeObject() {
		return nativeObject;
	}

	public String describe() {
		if (isStaticInstance) {
			return nativeClass.getCanonicalName();
		}
		return nativeObject != null ? nativeObject.toString() : "";
	}

	public boolean getBool() {
		return isStaticInstance || (nativeObject != null);
	}

	@Override
	public String toString() {
		return (isStaticInstance ? "Class " : "") + nativeClass.getCanonicalName()
				+ (nativeObject != null ? " " + nativeObject.toString() : (!isStaticInstance ? " <null>" : ""));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof NativeObject) {
			NativeObject o = (NativeObject) obj;
			return nativeClass.equals(o.nativeClass) && isStaticInstance == o.isStaticInstance
					&& Objects.equals(nativeObject, o.nativeObject);
		}
		return false;
	}

	////////////////////
	// Java bindings

	private static Class<?> getClassForName(QualifiedClassName qcn) {
		try {
			return Class.forName(qcn.getJavaClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private static Constructor<?> getConstructor(Class<?> nativeClass, List<Value> ctorParams) {
		for (Constructor<?> constructor : nativeClass.getConstructors()) {
			if (matchParameterTypes(constructor.getParameterTypes(), ctorParams)) {
				return constructor;
			}
		}
		return null;
	}

	private static boolean matchParameterTypes(Class<?>[] parameterTypes, List<Value> methodParams) {
		if (methodParams == null) {
			return parameterTypes.length == 0;
		}
		if (parameterTypes.length != methodParams.size()) {
			return false;
		}
		int i = 0;
		for (Value value : methodParams) {
			if (!matchParameterType(parameterTypes[i++], value)) {
				return false;
			}
		}
		return true;
	}

	private static boolean matchParameterType(Class<?> cls, Value value) {
		switch (value.getType()) {
			case NATIVE:
				return isAssignableFrom(cls, value.getNative().getNativeClass());
			case NUMBER:
				return isAssignableFrom(cls, LONG_CLASSES) || isAssignableFrom(cls, INT_CLASSES)
						|| isAssignableFrom(cls, SHORT_CLASSES) || isAssignableFrom(cls, BYTE_CLASSES)
						|| isAssignableFrom(cls, CHAR_CLASSES) || isAssignableFrom(cls, DOUBLE_CLASSES)
						|| isAssignableFrom(cls, FLOAT_CLASSES) || isAssignableFrom(cls, OTHER_NUMERIC_CLASSES);
			case STRING:
				return isAssignableFrom(cls, String.class);
			case ARRAY:
				return cls.isArray() || isAssignableFrom(cls, ARRAY_CLASSES);
			case BOOLEAN:
				return isAssignableFrom(cls, boolean.class, Boolean.class);
			case FUNCTION:
				return cls.isInterface() && cls.isAnnotationPresent(FunctionalInterface.class);
			case NULL:
				return !cls.isPrimitive();
			case MYSTERIOUS:
				return false;
			case OBJECT:
				return false;
		}
		return false;
	}

	private static Object[] convertValuesToJava(Class<?>[] types, List<Value> values, BlockContext ctx) {
		Object[] args = new Object[values.size()];
		int i = 0;
		for (Value value : values) {
			args[i] = convertValueToJava(types[i], value, ctx);
			i++;
		}
		return args;
	}

	private static Object convertValueToJava(Class<?> cls, Value value, BlockContext ctx) {
		switch (value.getType()) {
		case NATIVE:
			return value.getNative().getNativeObject();
		case NUMBER:
			if (isAssignableFrom(cls, BigDecimal.class)) {
				return value.getNumeric().asBigDecimal();
			} else if (isAssignableFrom(cls, BigInteger.class)) {
				return value.getNumeric().asBigDecimal().toBigInteger();
			} else if (isAssignableFrom(cls, DOUBLE_CLASSES)) {
				return value.getNumeric().asDouble();
			} else if (isAssignableFrom(cls, FLOAT_CLASSES)) {
				return (float) value.getNumeric().asDouble();
			} else if (isAssignableFrom(cls, LONG_CLASSES)) {
				return value.getNumeric().asLong();
			} else if (isAssignableFrom(cls, INT_CLASSES)) {
				return value.getNumeric().asInt();
			} else if (isAssignableFrom(cls, SHORT_CLASSES)) {
				return (short) value.getNumeric().asInt();
			} else if (isAssignableFrom(cls, BYTE_CLASSES)) {
				return (byte) value.getNumeric().asInt();
			} else if (isAssignableFrom(cls, CHAR_CLASSES)) {
				return (char) value.getNumeric().asInt();
			}
			return null;
		case STRING:
			return value.getString();
		case ARRAY:
			if (cls.isArray()) {
				List<Value> valueList = value.asListArray();
				Class<?> compType = cls.getComponentType();
				Object[] arr = (Object[]) Array.newInstance(compType, valueList.size());
				int i = 0;
				for (Value v : valueList) {
					arr[i++] = convertValueToJava(compType, v, ctx);
				}
				return arr;
			} else if (isAssignableFrom(cls, List.class)) {
				List<Value> valueList = value.asListArray();
				List<Object> arrList = new ArrayList<>(valueList.size());
				valueList.forEach(v -> arrList.add(convertValueToJava(Object.class, v, ctx)));
				return arrList;
			} else if (isAssignableFrom(cls, Map.class)) {
				Map<Value, Value> valueMap = value.asAssocArray();
				Map<Object, Object> arrMap = new HashMap<>();
				valueMap.forEach(
						(Value k, Value v) -> arrMap.put(convertValueToJava(Object.class, k, ctx), convertValueToJava(Object.class, v, ctx)));
				return arrMap;
			}
			return null;
		case FUNCTION:
			return RockstarFunctionWrapper.getProxyObject(cls, value.getFunctionBlock(), ctx);
		case BOOLEAN:
			return value.getBool();
		case NULL:
			return null;
		default:
			return null;
		}
	}

	private static boolean isAssignableFrom(Class<?> cls, Class<?>... classes) {
		for (int i = 0; i < classes.length; i++) {
			if (cls.isAssignableFrom(classes[i])) {
				return true;
			}
		}
		return false;
	}

	public Value callMethod(String functionName, List<Value> methodParams, BlockContext ctx) {
		functionName = functionName.replace(" ", "");
		// get method name
		Method method = getMethod(functionName, methodParams);
		if (method == null && methodParams.size() == 0) {
			// maybe a property name?
			Field field = getField(functionName);
			if (field != null) {
				try {
					Object rawValue = field.get(nativeObject);
					return convertPrimitiveFromJava(rawValue, field.getType());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RockstarRuntimeException("Error accessing native field " + functionName + " on class "
							+ nativeClass.getCanonicalName() + ": " + e.getMessage());
				}
			}
		}
		if (method == null) {
			throw new RockstarRuntimeException(
					"Unknown native method " + functionName + " on class " + nativeClass.getCanonicalName());
		}
		// convert values
		Object[] methodArgs = convertValuesToJava(method.getParameterTypes(), methodParams, ctx);

		// call method
		try {
			Object rawValue = method.invoke(nativeObject, methodArgs);
			return convertPrimitiveFromJava(rawValue, method.getReturnType());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RockstarRuntimeException("Cannot call native method " + functionName + " on class: "
					+ nativeClass.getCanonicalName() + " with params " + methodArgs);
		}
	}

	public static Value convertPrimitiveFromJava(Object rawValue, Class<?> returnClass) {
		// only primitive values (Long, Double, String, null) are converted,
		// other objects (BigDecimal, List, array, Map, etc) are wrapped into
		// NativeObject (also general objects)
		// Void converted to MYSTEROIUS, null to NULL
		if (returnClass.equals(Void.class) || returnClass.equals(void.class)) {
			return Value.MYSTERIOUS;
		} else if (rawValue == null) {
			return Value.NULL;
		} else if (rawValue instanceof String) {
			return Value.getValue(rawValue.toString());
		} else if (rawValue instanceof Double) {
			return Value.getValue(RockNumber.fromDouble((Double) rawValue));
		} else if (rawValue instanceof Float) {
			return Value.getValue(RockNumber.fromDouble((Float) rawValue));
		} else if (rawValue instanceof Long) {
			return Value.getValue(RockNumber.fromLong((Long) rawValue));
		} else if (rawValue instanceof Integer) {
			return Value.getValue(RockNumber.fromLong((Integer) rawValue));
		} else if (rawValue instanceof Short) {
			return Value.getValue(RockNumber.fromLong((Short) rawValue));
		} else if (rawValue instanceof Byte) {
			return Value.getValue(RockNumber.fromLong((Byte) rawValue));
		} else if (rawValue instanceof Character) {
			return Value.getValue(RockNumber.fromLong((Character) rawValue));
		} else if (rawValue instanceof Boolean) {
			return Value.getValue((Boolean) rawValue);
		}
		return Value.getValue(new NativeObject(rawValue.getClass(), rawValue));
	}

	private Method getMethod(String functionName, List<Value> methodParams) {
		Class<?> cls = getEffectiveClass();
		if (cls != null) {
			for (Method method : cls.getMethods()) {
				if (method.getName().equalsIgnoreCase(functionName)
						&& matchParameterTypes(method.getParameterTypes(), methodParams)) {
					return method;
				}
			}
		}
		return null;
	}

	private Field getField(String functionName) {
		Class<?> cls = getEffectiveClass();
		while (cls != null) {
			for (Field field : cls.getDeclaredFields()) {
				if (field.getName().equalsIgnoreCase(functionName)) {
					return field;
				}
			}
			cls = cls.getSuperclass();
		}
		return null;
	}
	
	private Class<?> getEffectiveClass() {
		return nativeObject == null 
				? nativeClass
				: nativeObject.getClass();
	}

	public Value getAsValue() {
		return convertFromJava(nativeObject, nativeClass);
	}

	public static Value convertFromJava(Object rawObject, Class<?> cls) {
		if (rawObject instanceof BigDecimal) {
			return Value.getValue(RockNumber.fromDouble(((BigDecimal) rawObject).doubleValue()));
		} else if (rawObject instanceof BigInteger) {
			return Value.getValue(RockNumber.fromLong(((BigInteger) rawObject).longValue()));
		} else if (rawObject instanceof List) {
			List<Value> newList = new ArrayList<>();
			for (Object o : (List<?>) rawObject) {
				newList.add(convertPrimitiveFromJava(o, Object.class));
			}
			return Value.getValue(newList);
		} else if (rawObject instanceof Map) {
			Map<Value, Value> newMap = new HashMap<>();
			for (Entry<?, ?> e : ((Map<?, ?>) rawObject).entrySet()) {
				newMap.put(convertPrimitiveFromJava(e.getKey(), Object.class), convertPrimitiveFromJava(e.getValue(), Object.class));
			}
			return Value.getValue(newMap);
		} else if (cls.isArray()) {
			Object[] arr = (Object[]) rawObject;
			List<Value> newList = new ArrayList<>(arr.length);
			Class<?> componentType = cls.getComponentType();
			for (int i = 0; i < arr.length; i++) {
				newList.add(convertPrimitiveFromJava(arr[i], componentType));
			}
			return Value.getValue(newList);
		}
		return convertPrimitiveFromJava(rawObject, cls);
	}

	public static NativeObject convertValueWithTypes(Value v, List<Class<?>> classList) {
		Object obj = convertValueWithTypes(v, classList, 0);
		return new NativeObject(classList.get(0), obj);
	}

	private static Object convertValueWithTypes(Value v, List<Class<?>> classes, int idx) {
		Class<?> baseClass = classes.get(idx);
		if (List.class.isAssignableFrom(baseClass)) {
			// List conversion
			List<Value> values = v.asListArray();
			if (baseClass.equals(List.class)) {
				// default List is ArrayList
				baseClass = ArrayList.class;
			}
			Constructor<?> listCtor = getConstructor(baseClass, null);
			if (listCtor == null) {
				throw new RockstarRuntimeException(baseClass.getCanonicalName() + " does not have parameterless constructor");				
			}
			List<Object> listObj;
			try {
				Object obj = listCtor.newInstance();
				@SuppressWarnings("unchecked")
				List<Object> listObjTmp = (List<Object>) obj;
				listObj = listObjTmp;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | ClassCastException e) {
				throw new RockstarRuntimeException("Invalid List class " + baseClass.getCanonicalName());
			}
			for (Value value : values) {
				listObj.add(convertValueWithTypes(value, classes, idx + 1));
			}
			return listObj;
		} else if (Map.class.isAssignableFrom(baseClass)) {
			// Map conversion
			Map<Value, Value> values = v.asAssocArray();
			if (baseClass.equals(Map.class)) {
				// default Map is HashMap
				baseClass = HashMap.class;
			}
			// create map instance
			Constructor<?> mapCtor = getConstructor(baseClass, null);
			Map<Object, Object> mapObj;
			try {
				Object obj = mapCtor.newInstance();
				@SuppressWarnings("unchecked")
				Map<Object, Object> mapObjTmp = (Map<Object, Object>) obj;
				mapObj = mapObjTmp;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | ClassCastException e) {
				throw new RockstarRuntimeException("Invalid List class " + baseClass.getCanonicalName());
			}
			if (!values.isEmpty()) {
				int keyIdx = idx + 1;
				int valueIdx = getFirstScalarIdxAfter(classes, keyIdx) + 1;
				// convert keys and values
				for (Entry<Value, Value> entry : values.entrySet()) {
					mapObj.put(convertValueWithTypes(entry.getKey(), classes, keyIdx),
							convertValueWithTypes(entry.getValue(), classes, valueIdx));
				}
			}
			return mapObj;
		} else if (Arrays.class.isAssignableFrom(baseClass)) {
			// array conversion
			List<Value> values = v.asListArray();
			Object[] arrObj = (Object[]) Array.newInstance(baseClass, values.size());
			int i = 0;
			for (Value value : values) {
				arrObj[i] = convertValueWithTypes(value, classes, idx + 1);
			}
			return arrObj;
		}

		// plain value with class specification
		return convertValueToJava(baseClass, v, null);
	}

	private static int getFirstScalarIdxAfter(List<Class<?>> classes, int idx) {
		int scalarCount = 1;
		for (int i = idx; i < classes.size(); i++) {
			Class<?> cls = classes.get(i);
			if (Map.class.isAssignableFrom(cls)) {
				// Map needs +1 scalar type
				scalarCount++;
			}
			if (!(List.class.isAssignableFrom(cls) || Arrays.class.isAssignableFrom(cls))) {
				scalarCount--;
				if (scalarCount == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	public static void setNativeDisabled(boolean disableNativeBinding) {
		NativeObject.isNativeDisabled = disableNativeBinding;		
	}

}
