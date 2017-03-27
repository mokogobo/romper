package camp.computer.construct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import camp.computer.util.console.Color;
import camp.computer.workspace.Manager;

public class Structure extends Address {

    // In Redis, primitive types has types and content; non-primitive has no content.
    // TODO: Use "features" object as a HashMap for non-primitive to reference features;
    // TODO:      ArrayList for primitive "list" types;
    // TODO:      String for primitive "text" types;
    // TODO:      Double for primitive "number" types;
    // TODO:      null for primitive "none" types

    public Type type = null; // The {@code Structure} used to create this Structure.

    // null for "none"
    // String for "text"
    // Double for "number"
    // [DELETE] Structure for non-primitive types
    // List for "list" (allocates ArrayList<Object>)
    // Map for non-primitive construct (allocates HashMap or TreeMap)
    public Class objectType = null;
    public Object object = null;

    // <TODO>
    // Update Tuple class to extend List. Rename to List (or Sequence)!
    //
    // (a) For non-primitive, don't store Map<String, Feature>. That's stored in the referenced Type!
    // (cont'd) Instead, create a Map<String, Structure> to store the states corresponding to
    // (cont'd) features. Then (b) remove the Structure.states HashMap. Leaves Structure as general
    // (cont'd) purpose data structuring primitive/construct.
    //
    // For "reference" type (or "index"), set type = "reference", classType = Structure.class,
    // (cont'd) object = referenced/indexed Construct.
    //
    // [A] Rename "Feature" to "Container" since it can contain any Type or Structure.
    // [B] For "feature", there are multiple types, so replace "Type type" with "List<Type> types"
    //     in Structure. "feature" is the only Structure that has more than one type (?).
    // [C] For feature, also add Structure.domain and Structure.listTypes (?) or use Map?
    //
    // Add custom List(...), Map(...)/Dictionary(...) classes with convenient constructors and for
    // (cont'd) use in the Manager.
    //
    // Add configuration to {@code Type}, but not Structure for now.
    //
    // Add constraint solver back into the new model (finally!).
    // </TODO>

    // This is only present for non-primitive types (that instantiate a Map)
    // TODO: Remove this after removing the State class.
    public HashMap<String, Structure> states = new HashMap<>(); // TODO: Remove? Remove setupConfiguration?

    // TODO: configuration(s) : assign state to multiple features <-- do this for _Container_ not Type


    // TODO:
    // 1. Use types, features, and states for non-console (structure) constructs (custom non-primitive constructs)
    // 2. For console states (i.e., to replace State), don't use features or states hashes. Store actual data in objectType and object (from State).

    private Structure(Type type) {

        this.type = type;

        // Allocate default object based on specified classType
        if (this.type.identifier.equals("none")) {
            this.objectType = null;
            this.object = null;
        } else if (this.type.identifier.equals("number")) {
            this.objectType = Double.class;
            this.object = 0; // TODO: Default to null (i.e., "none" structure)?
        } else if (this.type.identifier.equals("text")) {
            this.objectType = String.class;
            this.object = ""; // TODO: Default to null?
        } else if (this.type.identifier.equals("list")) {
            this.objectType = List.class;
            this.object = new ArrayList<>();
        } else if (this.type.identifier != null) {
            this.objectType = Map.class;
            this.object = new HashMap<String, Feature>();

            // Initialize each {@code Feature} to the default value of <em>none</em>.
            HashMap<String, Feature> features = (HashMap<String, Feature>) this.object;
            for (Feature feature : type.features.values()) {
                features.put(feature.identifier, feature);
                Type noneType = Type.request("none");
                Structure structure = Structure.create(noneType);
                states.put(feature.identifier, structure); // Initialize with only available types if there's only one available
            }
        }
    }

//    /**
//     * Returns {@code true} if {@code construct} is configured to store one or more
//     * <em>primitive</em> constructs. This configuration is determined to be present if the
//     * {@code construct} references a {@code Map}.
//     * @param construct
//     * @return
//     */
//    public static boolean isComposite(Structure construct) {
////        if (!Structure.isPrimitive(construct) && construct.objectType == Map.class && construct.object != null) {
//        if (construct.objectType == Map.class && construct.object != null) {
//            return true;
//        }
//        return false;
//    }

//    public static Structure create(TypeId type) {
//        Type type = Type.request(type);
//        if (type != null) {
//            Structure construct = Manager.getPersistentConstruct(type);
//            if (construct == null) {
//                // TODO: Check if default construct for classType already exists!
//                construct = new Structure(type);
//                long uid = Manager.add(construct);
//                return construct;
//            }
//            return construct;
//        }
//        return null;
//    }

    /**
     * Returns the number of {@code Structure}s that have a {@code type} <em>exactly</em>
     * identical to {@code Type} (i.e., the {@code Type} UUIDs are identical).
     *
     * @param type
     * @return
     */
    public static int count(Type type) {
        int count = 0;
        List<Structure> structureList = Manager.get(Structure.class);
        Iterator<Structure> structureIterator = structureList.iterator();
        while (structureIterator.hasNext()) {
            Structure structure = structureIterator.next(); // must be called before you can call i.remove()
            if (structure.type.identifier.equals(type.identifier)) {
            // if (structure.type == type) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a {@code List} of the {@code Structure}s with the specified {@code Type}.
     *
     * @return
     */
    public static List<Structure> list(Type type) {
        List<Structure> structureList = Manager.get(Structure.class);
        Iterator<Structure> structureIterator = structureList.iterator();
        while (structureIterator.hasNext()) {
            Structure structure = structureIterator.next(); // must be called before you can call i.remove()
            if (!structure.type.identifier.equals(type.identifier)) {
            // if (structure.type == type) {
                structureIterator.remove();
            }
        }
        return structureList;
    }

    public static Structure create(Type type) {
        if (type != null) {
//            Structure structure = Manager.getPersistentConstruct(type);
            Structure structure = Manager.getPersistentConstruct(type);
            if (structure == null) {
                // TODO: Check if default structure for classType already exists!
                structure = new Structure(type);
                long uid = Manager.add(structure);
                return structure;
            }
            return structure;
        }
        return null;
    }

//    // TODO: public static Structure create(Double number)
//    public static Structure create(String text) {
//        Structure construct = null;
//       // TODO:
//        return construct;
//    }

    public static Structure create(String text) {

        if (Expression.isText(text)) {
            Type type = Type.request("text");
            Structure newTextStructure = new Structure(type);

            newTextStructure.object = text.substring(1, text.length() - 1);

            long uid = Manager.add(newTextStructure);
            return newTextStructure;
        }
        return null;
    }

    public static Structure create(List list) { // previously, REFACTOR_getList(...)

        Type type = Type.request("list");
        Structure newListStructure = new Structure(type);

        // Copy elements into construct list.
        List constructList = (List) newListStructure.object;
        for (int i = 0; i < list.size(); i++) {
            constructList.add(list.get(i));
        }

        long uid = Manager.add(newListStructure);
        return newListStructure;

    }

    /**
     * Creates a {@code Structure} by specified feature change. Creates {@code Structure} if it
     * doesn't exist in the persistent store.
     *
     * @param baseStructure The reference {@code Structure} for the feature replacement.
     * @param targetFeature The feature to replace in {@code baseStructure} with {@code replacementStructure}.
     * @param replacementStructure The {@code Structure} to assign to the feature identified by {@code targetFeature}.
     * @return
     */
    public static Structure create(Structure baseStructure, String targetFeature, Structure replacementStructure) {

//        Type type = Type.request(baseStructure.type);
//        Type type = Type.request(baseStructure.type);
//        Structure newContruct = new Structure(type);
        Structure newContruct = new Structure(baseStructure.type);

        // Copy states from source Structure.
        for (String featureIdentifier : baseStructure.states.keySet()) {
            if (featureIdentifier.equals(targetFeature)) {
                newContruct.states.put(targetFeature, replacementStructure);
            } else {
                newContruct.states.put(featureIdentifier, baseStructure.states.get(featureIdentifier));
            }
        }

        long uid = Manager.add(newContruct);
        return newContruct;

    }

    /**
     * If the State does not exist (in cache or persistent store), then returns null.
     *
     * Retrieves State from persistent store if it exists! Also caches it!
     *
     * <strong>Examples of {@code Expression}:</strong>
     *
     * none
     *
     * 'foo'
     * text('foo')
     * text.'foo'
     * text(id:34)
     * text.id=34
     *
     * 66
     * number(66)
     * number(66)
     * number(id:12)
     * number.id=12
     *
     * text(id:34), 'foo', 'bar'
     * list(text(id:34), 'foo', 'bar')
     * list(id:44)
     *
     * port(id:99)
     */
    public static Structure request2(String expression) { // previously, getPersistentConstruct
        Type structureType = Type.request(expression);
        if (structureType != null) {

            if (structureType == Type.request("none")) {
                // Look for existing (persistent) state for the given expression
                List<Structure> structureList = Manager.get(Structure.class);
                for (int i = 0; i < structureList.size(); i++) {
                    Structure structure = structureList.get(i);
                    if (structure.type == Type.request("none") && structure.objectType == null && structure.object == null) {
                        return structure;
                    }
                }
                // State wasn't found, so create a new one and return it
                Type type = Type.request(structureType.identifier);
                return Structure.create(type);
//                return Structure.create(constructTypeId);
                /*
                if (construct == null) {
                    // TODO: Store in the database
                    construct = Structure.create(constructTypeId);
                }
                return construct;
                */
            } else if (structureType == Type.request("text")) {
                // e.g.,
                // [ ] 'foo'
                // [ ] text('foo')
                // [ ] text(id:234)

                // Look for existing (persistent) state for the given expression
                List<Structure> structureList = Manager.get(Structure.class);
                for (int i = 0; i < structureList.size(); i++) {
                    Structure structure = structureList.get(i);
                    String textContent = "";
                    if (expression.startsWith("'") && expression.endsWith("'")) {
                        textContent = expression.substring(1, expression.length() - 1);
                    }
                    if (structure.type == Type.request("text") && structure.objectType == String.class && textContent.equals(structure.object)) {
//                        if (structure.classType == Type.request("text") && structure.objectType == String.class && ((textContent == null && structure.object == null) || textContent.equals(structure.object))) {
                        return structure;
                    }
                }
                // State wasn't found, so create a new one and return it
                // TODO: Store in the database
                Structure structure = null;
                if (expression.startsWith("'") && expression.endsWith("'")) {
                    Type typeType = Type.request(structureType.identifier);
                    structure = new Structure(typeType);
                    long uid = Manager.add(structure);
                    structure.object = expression.substring(1, expression.length() - 1);
                } else {
                    Type type = Type.request(structureType.identifier);
                    structure = Structure.create(type);
                    structure.object = "";
                }
                return structure;
            } else if (structureType == Type.request("list")) {

                // TODO: Same existence-checking procedure as for construct? (i.e., look up "list(id:34)")
                // TODO: Also support looking up by construct permutation contained in list?

                // Look for existing (persistent) state for the given expression
                List<Address> identiferList = Manager.get();
                for (int i = 0; i < identiferList.size(); i++) {
                    if (identiferList.get(i).getClass() == Structure.class) {
                        Structure structure = (Structure) identiferList.get(i);
                        if (structure.type == Type.request("list") && structure.objectType == List.class && structure.object != null) {
                            // TODO: Look for permutation of a list (matching list of constructs)?
                            return structure;
                        }
                    }
                }

            } else {

                if (Expression.isAddress(expression)) {

//                    String typeIdentifierToken = expression.substring(0, expression.indexOf("(")).trim(); // text before '('
//                    String addressTypeToken = expression.substring(expression.indexOf("(") + 1, expression.indexOf(":")).trim(); // text between '(' and ':'
//                    String addressToken = expression.substring(expression.indexOf(":") + 1, expression.indexOf(")")).trim(); // text between ':' and ')'
                    String[] tokens = expression.split("\\.");
                    String typeIdentifierToken = tokens[0];
                    String addressTypeToken = tokens[1];
                    String addressToken = tokens[2];

                    long uid = Long.parseLong(addressToken.trim());

                    Address address = Manager.get(uid);
//                    if (address != null) {
//                        if (address.getClass() == Structure.class) {
//                            State state = State.getState(stateType);
//                            state.object = address;
//                            return state;
//                        }
//                    }

                    if (address != null) {
                        return (Structure) address;
                    }


//                    // Look for existing (persistent) state for the given expression
//                    if (address != null) {
//                        List<Address> identiferList = Manager.request();
//                        for (int i = 0; i < identiferList.size(); i++) {
//                            if (identiferList.request(i).getClass() == Structure.class) {
//                                Structure structure = (Structure) identiferList.request(i);
////                            String textContent = expression.substring(1, expression.length() - 1);
//                                // TODO: Also check TypeId?
//                                if (structure.objectType == Map.class && structure.object != null) {
////                                        && structure.object == address) {
////                                        && structure.object == address) {
//                                    for (Structure featureConstruct : structure.states.values()) {
//                                        if (features.containsValue(address)) { // TODO: iterate through features to see if contains feature...
//                                            return structure;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }

                }

                // Create new structure since a persistent one wasn't found for the expression
                Structure structure = null;
                if (structure == null) {

                    // Create new State
                    // TODO: Add new state to persistent store

                    Type typeType = Type.request(structureType.identifier);
                    structure = new Structure(typeType);
                    long uid = Manager.add(structure);
//                    structure.object = expression.substring(1, expression.length() - 1);

//                    String typeIdentifierToken = expression.substring(0, expression.indexOf("(")).trim(); // text before '('
//                    String addressTypeToken = expression.substring(expression.indexOf("(") + 1, expression.indexOf(":")).trim(); // text between '(' and ':'
//                    String addressToken = expression.substring(expression.indexOf(":") + 1, expression.indexOf(")")).trim(); // text between ':' and ')'
//
//                    long uid = Long.parseLong(addressToken.trim());
//                    Address address = Manager.request(uid);
//                    if (address != null) {
//                        structure = Structure.request(constructTypeId);
//                        structure.object = address;
//                        return structure;
//                    } else {
//                        System.out.println(Error.request("Error: " + expression + " does not exist."));
//                    }
                }
                return structure;
            }
        }

        return null;
    }

    // e.g.,
    // none
    // none.id=3
    // 'foo'
    // text.id=34
    // port.id=44
    // port.id=23, port.id=44, port.id=12
    //
    // text => default text structure
    // list => default list structure
    // port => default port structure

    // TODO: Refactor to separate request, create, requestOrCreate
    public static Structure request(String expression) { // previously, getPersistentConstruct

        // Search for <em>address</em> (default structure or structure address).
        List<Structure> structureList = Manager.get(Structure.class);
        for (int i = 0; i < structureList.size(); i++) {
            if (structureList.get(i).type.identifier.equals(expression)
                    && structureList.get(i).type.features == null) {
                // Return the <em>default</em> {@code Type} for the address.
                return structureList.get(i);
            }
        }

        if (Expression.isAddress(expression)) {

//                    String typeIdentifierToken = expression.substring(0, expression.indexOf("(")).trim(); // text before '('
//                    String addressTypeToken = expression.substring(expression.indexOf("(") + 1, expression.indexOf(":")).trim(); // text between '(' and ':'
//                    String addressToken = expression.substring(expression.indexOf(":") + 1, expression.indexOf(")")).trim(); // text between ':' and ')'
//                    String[] tokens = expression.split("\\.");
//                    String typeIdentifierToken = tokens[0];
//                    String addressTypeToken = tokens[1];
//                    String addressToken = tokens[2];
                    // TODO: Test this case and all other cases (after Type refactoring from old Type/Concept/Construct paradigm)
                    String[] expressionTokens = expression.split("\\.");
                    String typeToken = expressionTokens[0];
                    long id = Long.parseLong(expressionTokens[1].split("=")[1]);

//                    long uid = Long.parseLong(addressToken.trim());

                    Address address = Manager.get(id);
                    if (address != null) {
                        if (address.getClass() == Structure.class) {
                            // TODO: Check that type matches type identifier!
                            return (Structure) address;
                        } else {
                            return null; // Return {@code null} if class isn't Structure.
                        }
                    }

//                    if (address != null) {
//                        return (Structure) address;
//                    }


//                    // Look for existing (persistent) state for the given expression
//                    if (address != null) {
//                        List<Address> identiferList = Manager.request();
//                        for (int i = 0; i < identiferList.size(); i++) {
//                            if (identiferList.request(i).getClass() == Structure.class) {
//                                Structure structure = (Structure) identiferList.request(i);
////                            String textContent = expression.substring(1, expression.length() - 1);
//                                // TODO: Also check TypeId?
//                                if (structure.objectType == Map.class && structure.object != null) {
////                                        && structure.object == address) {
////                                        && structure.object == address) {
//                                    for (Structure featureConstruct : structure.states.values()) {
//                                        if (features.containsValue(address)) { // TODO: iterate through features to see if contains feature...
//                                            return structure;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }

                }

//                // Create new structure since a persistent one wasn't found for the expression
//                Structure structure = null;
//                if (structure == null) {
//
//                    // Create new State
//                    // TODO: Add new state to persistent store
//
//                    Type typeType = Type.request(type.identifier);
//                    structure = new Structure(typeType);
//                    long uid = Manager.add(structure);
////                    structure.object = expression.substring(1, expression.length() - 1);
//
////                    String typeIdentifierToken = expression.substring(0, expression.indexOf("(")).trim(); // text before '('
////                    String addressTypeToken = expression.substring(expression.indexOf("(") + 1, expression.indexOf(":")).trim(); // text between '(' and ':'
////                    String addressToken = expression.substring(expression.indexOf(":") + 1, expression.indexOf(")")).trim(); // text between ':' and ')'
////
////                    long uid = Long.parseLong(addressToken.trim());
////                    Address address = Manager.request(uid);
////                    if (address != null) {
////                        structure = Structure.request(constructTypeId);
////                        structure.object = address;
////                        return structure;
////                    } else {
////                        System.out.println(Error.request("Error: " + expression + " does not exist."));
////                    }
//                }
//                return structure;

        Type type = Type.request(expression);
        if (type != null) {

            if (type == Type.request("none")) {
                // Look for existing (persistent) state for the given expression
//                List<Structure> structureList = Manager.get(Structure.class);
                structureList = Manager.get(Structure.class);
                for (int i = 0; i < structureList.size(); i++) {
                    Structure structure = structureList.get(i);
                    if (structure.type == Type.request("none") && structure.objectType == null && structure.object == null) {
                        return structure;
                    }
                }
                // State wasn't found, so create a new one and return it
                Type type2 = Type.request(type.identifier);
                return Structure.create(type2);
//                return Structure.create(constructTypeId);
                /*
                if (construct == null) {
                    // TODO: Store in the database
                    construct = Structure.create(constructTypeId);
                }
                return construct;
                */
            } else if (type == Type.request("text")) {
                // e.g.,
                // [ ] 'foo'
                // [ ] text('foo')
                // [ ] text(id:234)

                // Look for existing (persistent) state for the given expression
//                List<Structure> structureList = Manager.get(Structure.class);
                structureList = Manager.get(Structure.class);
                for (int i = 0; i < structureList.size(); i++) {
                    Structure structure = structureList.get(i);
                    String textContent = "";
                    if (expression.startsWith("'") && expression.endsWith("'")) {
                        textContent = expression.substring(1, expression.length() - 1);
                    }
                    if (structure.type == Type.request("text") && structure.objectType == String.class && textContent.equals(structure.object)) {
//                        if (structure.classType == Type.request("text") && structure.objectType == String.class && ((textContent == null && structure.object == null) || textContent.equals(structure.object))) {
                        return structure;
                    }
                }
                // State wasn't found, so create a new one and return it
                // TODO: Store in the database
                Structure structure = null;
                if (expression.startsWith("'") && expression.endsWith("'")) {
                    Type typeType = Type.request(type.identifier);
                    structure = new Structure(typeType);
                    long uid = Manager.add(structure);
                    structure.object = expression.substring(1, expression.length() - 1);
                } else {
                    Type type2 = Type.request(type.identifier);
                    structure = Structure.create(type2);
                    structure.object = "";
                }
                return structure;
            } else if (type == Type.request("list")) {

                // TODO: Same existence-checking procedure as for construct? (i.e., look up "list(id:34)")
                // TODO: Also support looking up by construct permutation contained in list?

                // Look for existing (persistent) state for the given expression
                List<Address> identiferList = Manager.get();
                for (int i = 0; i < identiferList.size(); i++) {
                    if (identiferList.get(i).getClass() == Structure.class) {
                        Structure structure = (Structure) identiferList.get(i);
                        if (structure.type == Type.request("list") && structure.objectType == List.class && structure.object != null) {
                            // TODO: Look for permutation of a list (matching list of constructs)?
                            return structure;
                        }
                    }
                }

            } else {

//                if (Expression.isAddress(expression)) {
//
////                    String typeIdentifierToken = expression.substring(0, expression.indexOf("(")).trim(); // text before '('
////                    String addressTypeToken = expression.substring(expression.indexOf("(") + 1, expression.indexOf(":")).trim(); // text between '(' and ':'
////                    String addressToken = expression.substring(expression.indexOf(":") + 1, expression.indexOf(")")).trim(); // text between ':' and ')'
////                    String[] tokens = expression.split("\\.");
////                    String typeIdentifierToken = tokens[0];
////                    String addressTypeToken = tokens[1];
////                    String addressToken = tokens[2];
//                    // TODO: Test this case and all other cases (after Type refactoring from old Type/Concept/Construct paradigm)
//                    String[] expressionTokens = expression.split("\\.");
//                    String typeToken = expressionTokens[0];
//                    long id = Long.parseLong(expressionTokens[1].split("=")[1]);
//
////                    long uid = Long.parseLong(addressToken.trim());
//
//                    Address address = Manager.get(id);
//                    if (address != null) {
//                        if (address.getClass() == Structure.class) {
//                            // TODO: Check that type matches type identifier!
//                            return (Structure) address;
//                        } else {
//                            return null; // Return {@code null} if class isn't Structure.
//                        }
//                    }
//
////                    if (address != null) {
////                        return (Structure) address;
////                    }
//
//
////                    // Look for existing (persistent) state for the given expression
////                    if (address != null) {
////                        List<Address> identiferList = Manager.request();
////                        for (int i = 0; i < identiferList.size(); i++) {
////                            if (identiferList.request(i).getClass() == Structure.class) {
////                                Structure structure = (Structure) identiferList.request(i);
//////                            String textContent = expression.substring(1, expression.length() - 1);
////                                // TODO: Also check TypeId?
////                                if (structure.objectType == Map.class && structure.object != null) {
//////                                        && structure.object == address) {
//////                                        && structure.object == address) {
////                                    for (Structure featureConstruct : structure.states.values()) {
////                                        if (features.containsValue(address)) { // TODO: iterate through features to see if contains feature...
////                                            return structure;
////                                        }
////                                    }
////                                }
////                            }
////                        }
////                    }
//
//                }
//
//                // Create new structure since a persistent one wasn't found for the expression
//                Structure structure = null;
//                if (structure == null) {
//
//                    // Create new State
//                    // TODO: Add new state to persistent store
//
//                    Type typeType = Type.request(type.identifier);
//                    structure = new Structure(typeType);
//                    long uid = Manager.add(structure);
////                    structure.object = expression.substring(1, expression.length() - 1);
//
////                    String typeIdentifierToken = expression.substring(0, expression.indexOf("(")).trim(); // text before '('
////                    String addressTypeToken = expression.substring(expression.indexOf("(") + 1, expression.indexOf(":")).trim(); // text between '(' and ':'
////                    String addressToken = expression.substring(expression.indexOf(":") + 1, expression.indexOf(")")).trim(); // text between ':' and ')'
////
////                    long uid = Long.parseLong(addressToken.trim());
////                    Address address = Manager.request(uid);
////                    if (address != null) {
////                        structure = Structure.request(constructTypeId);
////                        structure.object = address;
////                        return structure;
////                    } else {
////                        System.out.println(Error.request("Error: " + expression + " does not exist."));
////                    }
//                }
//                return structure;
            }
        }

        return null;
    }

    /**
     * Request the <em>list</em> {@code Structure} that contains the same sequence of
     * {@code Structure}s as specified in {@code list}.
     *
     * @param list
     * @return
     */
    public static Structure request(List list) { // previously, getPersistentListConstruct

//        TypeId type = Type.request("list");
        Type type = Type.request("list");

        // Look for persistent "empty list" object (i.e., the default list).
        List<Address> identiferList = Manager.get();
        for (int i = 0; i < identiferList.size(); i++) {
            if (identiferList.get(i).getClass() == Structure.class) {
                Structure candidateStructure = (Structure) identiferList.get(i);

                if (candidateStructure.type == type && candidateStructure.objectType == List.class && candidateStructure.object != null) {
                    // LIST


                    // Check (1) if constructs are based on the same specified type version, and
                    //       (2) same list of constructs.
                    List candidateConstructList = (List) candidateStructure.object;
//                    List currentConstructList = (List) currentConstruct.object;
                    List currentConstructList = list;

                    // Compare identifer, types, domain, listTypes
                    // TODO: Move comparison into Type.hasConstruct(type, construct);
                    boolean isConstructMatch = true;
                    if (candidateConstructList.size() != currentConstructList.size()) {
                        isConstructMatch = false;
                    } else {

                        // Compare candidate list (from repository) with the requested list.
                        for (int j = 0; j < currentConstructList.size(); j++) {
                            if (!candidateConstructList.contains(currentConstructList.get(j))) {
                                isConstructMatch = false;
                            }
                        }

//                        // Compare candidate construct (from repository) with the current construct being updated.
//                        for (String featureIdentifier : currentConstructFeatures.keySet()) {
//                            if (featureIdentifier.equals(featureToReplace)) {
//                                if (!candidateConstructFeatures.containsKey(featureIdentifier)
//                                        || !candidateStructure.states.containsKey(featureIdentifier)
//                                        || candidateStructure.states.request(featureIdentifier) != featureConstructReplacement) {
////                                        || !candidateConstructFeatures.containsValue(featureConstructReplacement)) {
//                                    isConstructMatch = false;
//                                }
//                            } else {
//                                if (!candidateConstructFeatures.containsKey(featureIdentifier)
//                                        || !candidateStructure.states.containsKey(featureIdentifier)
//                                        || candidateStructure.states.request(featureIdentifier) != currentConstruct.states.request(featureIdentifier)) {
////                                        || !candidateConstructFeatures.containsValue(type.features.request(featureIdentifier))) {
//                                    isConstructMatch = false;
//                                }
//                            }
//                        }
//
//                        // TODO: Additional checks...

                    }

                    if (isConstructMatch) {
                        return candidateStructure;
                    }


                    // TODO: Look for permutation of a list (matching list of constructs)?
//                    return construct;

                }
            }
        }

        // Create new Structure if got to this point because an existing one was not found
//        Structure newReplacementStructure = Structure.create(currentConstruct, featureToReplace, featureConstructReplacement);
        Structure newReplacementStructure = Structure.create(list);
        if (newReplacementStructure != null) {
            return newReplacementStructure;
        }

        // TODO: Iterate through constructs searching for one that matches the default construct hierarchy for the specified type (based on the Type used to create it).
        return null;

    }


    /**
     * Requests a {@code Structure} by feature change. Creates {@code Structure} if it doesn't
     * exist in the persistent store.
     *
     * Returns the persistent {@code Structure}, if exists, that would result from applying
     * {@code expression} to the specified {@code construct}.
     *
     * If no such {@code Structure} exists, returns {@code null}.
     */
//    public static Structure getPersistentConstruct(Structure construct, String expression) {
//    public static Structure getPersistentConstruct(Structure construct, Feature feature, Structure featureStructureReplacement) {
    public static Structure request(Structure currentStructure, String featureToReplace, Structure featureStructureReplacement) {

//        TypeId type = currentStructure.type; // Structure type
        Type type2 = currentStructure.type; // Structure type

        // Look for persistent "empty list" object (i.e., the default list).
        List<Address> identiferList = Manager.get();
        for (int i = 0; i < identiferList.size(); i++) {
            if (identiferList.get(i).getClass() == Structure.class) {
                Structure candidateStructure = (Structure) identiferList.get(i);

                if (candidateStructure.type == type2 && candidateStructure.objectType == List.class && candidateStructure.object != null) {
                    // LIST


                    // Check (1) if constructs are based on the same specified type version, and
                    //       (2) same list of constructs.
                    List candidateConstructList = (List) candidateStructure.object;
                    List currentConstructList = (List) currentStructure.object;

                } else if (candidateStructure.type == type2 && candidateStructure.objectType == Map.class && candidateStructure.object != null) {
//                } else if (Structure.isComposite(construct)) {
                    // HASHMAP

                    // Check (1) if constructs are based on the same specified type version, and
                    //       (2) same set of features and assignments to constructs except the specified feature to change.
                    HashMap<String, Feature> candidateConstructFeatures = (HashMap<String, Feature>) candidateStructure.object;
                    HashMap<String, Feature> currentConstructFeatures = (HashMap<String, Feature>) currentStructure.object;

                    // Compare identifer, types, domain, listTypes
                    // TODO: Move comparison into Type.hasConstruct(type, construct);
                    boolean isConstructMatch = true;
                    if (candidateConstructFeatures.size() != currentConstructFeatures.size()) {
                        isConstructMatch = false;
                    } else {

                        // Compare candidate construct (from repository) with the current construct being updated.
                        for (String featureIdentifier : currentConstructFeatures.keySet()) {
                            if (featureIdentifier.equals(featureToReplace)) {
                                if (!candidateConstructFeatures.containsKey(featureIdentifier)
                                        || !candidateStructure.states.containsKey(featureIdentifier)
                                        || candidateStructure.states.get(featureIdentifier) != featureStructureReplacement) {
//                                        || !candidateConstructFeatures.containsValue(featureStructureReplacement)) {
                                    isConstructMatch = false;
                                }
                            } else {
                                if (!candidateConstructFeatures.containsKey(featureIdentifier)
                                        || !candidateStructure.states.containsKey(featureIdentifier)
                                        || candidateStructure.states.get(featureIdentifier) != currentStructure.states.get(featureIdentifier)) {
//                                        || !candidateConstructFeatures.containsValue(type.features.request(featureIdentifier))) {
                                    isConstructMatch = false;
                                }
                            }
                        }

                        // TODO: Additional checks...

                    }

                    if (isConstructMatch) {
                        return candidateStructure;
                    }


                    // TODO: Look for permutation of a list (matching list of constructs)?
//                    return construct;
                }
            }
        }

        // Create new Structure if got to this point because an existing one was not found
        Structure newReplacementStructure = Structure.create(currentStructure, featureToReplace, featureStructureReplacement);
        if (newReplacementStructure != null) {
            return newReplacementStructure;
        }

        // TODO: Iterate through constructs searching for one that matches the default construct hierarchy for the specified type (based on the Type used to create it).
        return null;

    }

    public static Feature getFeature(Structure structure, String featureIdentifier) {
        HashMap<String, Feature> features = (HashMap<String, Feature>) structure.object;
        if (features.containsKey(featureIdentifier)) {
            return features.get(featureIdentifier);
        }
        return null;
    }

    // If listType is "any", allow anything to go in the list
    // if listType is "text", only allow text to be placed in the list
    // if listType is specific "text" values, only allow those values in the list
//    public void set(String featureIdentifier, String expression) {
//
//        // TODO: Check if classType can use "set"
//
//        HashMap<String, Feature> features = (HashMap<String, Feature>) this.object;
//
//        if (features.containsKey(featureIdentifier)) {
//
//            TypeId constructType = Type.request(expression);
//            Feature feature = features.request(featureIdentifier);
////            if (feature.types == null || feature.types.contains(constructType)) {
//            if (feature.types.size() == 0 || feature.types.contains(constructType)) {
//
//                /*
//                // Get feature's current state
//                State state = states.request(featureIdentifier);
//
//                if (stateType == Type.request("none")) {
//
//                    // Remove the types of the stored object
//                    if (state == null) {
//                        state = State.getState(stateType);
//                    } else if (state.types != stateType) {
////                        featureContent.objectType = null;
////                        featureContent.object = null;
//                        state = State.getState(stateType);
//                    }
//
//                } else if (stateType == Type.request("list")) {
//
//                    // Change the types of the stored object if it is not a list
//                    if (state == null) {
//                        state = State.getState(stateType);
//                    } else if (state.types != stateType) {
////                        featureContent.objectType = List.class;
////                        featureContent.object = new ArrayList<>();
//                        state = State.getState(stateType);
//                    }
//
//                    // Update the object
//
//                } else if (stateType == Type.request("text")) {
//
//                    // Change the types of the stored object if it is not a string (for text)
////                    if (state == null) {
////                        state = State.getPersistentState(stateType);
////                    } else if (state.types != stateType) {
//////                        featureContent.objectType = String.class;
//////                        featureContent.object = null;
////                        state = State.getPersistentState(stateType);
////                    }
//
////                    if (Content.isText((String) object)) {
//                   state = State.getState((String) expression);
////                    if (feature.domain == null || feature.domain.contains(stateExpression)) {
//                    if (feature.domain == null || feature.domain.contains(state)) { // TODO: Make sure 'contains' works!
////                        state.object = (String) stateExpression;
//                        states.put(featureIdentifier, state);
//                    } else {
//                        System.out.println(Application.ANSI_RED + "Error: Specified text is not in the feature's domain." + Application.ANSI_RESET);
//                    }
////                    } else {
////                        System.out.println("Error: Cannot assign non-text to text feature.");
////                    }
//
//                } else {
//
//
//                }
//                */
//
//                if (constructType == Type.request("none")) {
//
//                    Structure construct = Structure.request(expression);
//
//                    if (feature.domain == null || feature.domain.contains(construct)) { // TODO: Make sure 'contains' works!
//                        states.put(featureIdentifier, construct);
//                        // TODO: Update Structure in database
//                    } else {
//                        System.out.println(Color.ANSI_RED + "Error: Specified text is not in the feature's domain." + Color.ANSI_RESET);
//                    }
//
////                } else if (stateType == Type.request("text")) {
////
////                    State state = State.getState(expression);
////
////                    if (state != null) {
////                        if (feature.domain == null || feature.domain.contains(state)) { // TODO: Make sure 'contains' works!
////                            states.put(featureIdentifier, state);
////                            // TODO: Update Structure in database
////                        } else {
////                            System.out.println(Application.ANSI_RED + "Error: Specified text is not in the feature's domain." + Application.ANSI_RESET);
////                        }
////                    } else {
////                        System.out.println(Application.ANSI_RED + "Error: Interpreter error. State is null." + Application.ANSI_RESET);
////                    }
//
//                } else if (constructType == Type.request("list")) {
//
//                    // TODO: Allow lists to be assigned? Yes!
//                    System.out.println(Color.ANSI_RED + "Error: Cannot SET on a list. (This might change!)." + Color.ANSI_RESET);
//
//                } else {
//
////                    State state = State.getState(expression);
////
////                    // Add to the list in memory
////                    // TODO: if (state != null && state != states.request(featureIdentifier)) {
////                    if (state != null) {
////                        if (feature.domain == null || feature.domain.contains(state)) { // TODO: Update domain to contain State objects so it can contain port and other Constructs
////                            State featureState = states.request(featureIdentifier);
////                            Structure featureConstruct = (Structure) featureState.object;
//////                        contents.request(tag).state.object = (String) object;
////                            featureConstruct.request(state);
////                        } else {
////                            System.out.println(Application.ANSI_RED + "Error: Specified " + stateType + " is not in the feature's domain." + Application.ANSI_RESET);
////                        }
////                    }
//
//                    Structure construct = Structure.request(expression);
//
//                    if (construct != null) {
//                        if (feature.domain == null || feature.domain.contains(construct)) { // TODO: Make sure 'contains' works!
//                            states.put(featureIdentifier, construct);
//                            // TODO: Update Structure in database
//                        } else {
//                            System.out.println(Color.ANSI_RED + "Error: Specified text is not in the feature's domain." + Color.ANSI_RESET);
//                        }
//                    } else {
//                        System.out.println(Color.ANSI_RED + "Error: Interpreter error. State is null." + Color.ANSI_RESET);
//                    }
//
////                    System.out.println(Application.ANSI_RED + "Error: Feature types mismatches object types." + Application.ANSI_RESET);
//
//                }
//            }
//
//
//            /*
//            if (contents.request(tag).types == Type.request("none")) {
//                // TODO: Can't assign anything to the feature object
//                System.out.println("Error: Cannot assign feature with types 'none'.");
//            } else if (contents.request(tag).types == Type.request("any")) {
//                // TODO: Verify that this is correct!
//                contents.request(tag).object = object;
//            } else if (contents.request(tag).types == Type.request("list")) {
//                List contentList = (List) contents.request(tag).object;
//
//                // TODO: Check types of list contents and restrict to the types (or any if "any")
//                // TODO: If specific text tokens are allowed AS WELL AS text construct, text construct subsumes the tokens and the tokens are not included in the domain
//
////                if (contents.request(address).listType == Type.request("text")) {
//                if (contents.request(tag).listTypes.contains(Type.request("text"))) {
//                    if (Content.isText((String) object)) {
//                        contentList.request(object);
//                    } else {
//                        System.out.println("Error: Cannot request non-text to list (only can contain text).");
//                    }
////                } else if (contents.request(address).listType == Type.request("construct")) {
////                } else if (contents.request(address).listTypes.contains(Type.request("construct"))) {
//                } else {
//                    // TODO: Determine if the construct object is allowed into the list based on the specific types!
//                    contentList.request(object);
//                }
//            } else if (contents.request(tag).types == Type.request("text")) {
//                if (Content.isText((String) object)) {
//                    contents.request(tag).object = (String) object;
//                } else {
//                    System.out.println("Error: Cannot assign non-text to text feature.");
//                }
//            } else {
//                contents.request(tag).object = object;
//            }
//            */
//        }
//    }

//    public Feature request(String tag) {
//        if (features.containsKey(tag)) {
//            return features.request(tag);
//        }
//        return null;
//    }

    // TODO: request <list-feature-address> : <object>

//    /**
//     * Adds a {@code State} to a <em>list</em> {@code Structure}, which is a {@code Structure} with
//     * a {@code TypeId} uniquely identified by its {@code address} equal to {@code "list"}.
//     *
//     * {@code expression} is a <em>state expression</em>.
//     *
//     * @param featureIdentifier
//     * @param expression
//     */
//    public void insert(String featureIdentifier, String expression) {
//        if (features.containsKey(featureIdentifier)) {
//            Feature feature = features.request(featureIdentifier);
//            Structure featureState = states.request(featureIdentifier);
//
//            // Check if feature can be a list
//            if (!feature.types.contains(Type.request("list"))) {
//                System.out.println(Color.ANSI_RED + "Error: Cannot add to a non-list." + Color.ANSI_RESET);
//                return;
//            }
//
//            // Check if feature is currently configured as a list
//            if (featureState.type != Type.request("list")) {
//                // Change the types of the stored object if it is not a list
//                if (featureState == null) {
//                    featureState = Structure.create(Type.request("list"));
//                } else if (featureState.type != Type.request("list")) {
//                    featureState = Structure.create(Type.request("list"));
//                }
//            }
//
//            // Add the object to the list
//            TypeId stateType = Type.request((String) expression);
//            if (stateType != null
//                    && (feature.listTypes == null || feature.listTypes.contains(stateType))) {
//
//                if (stateType == Type.request("none")) {
//
////                    // Remove the types of the stored object
////                    if (featureContent.state == null) {
////                        featureContent.state = new State(objectType);
////                    } else if (featureContent.state.types != objectType) {
//////                        featureContent.objectType = null;
//////                        featureContent.object = null;
////                        featureContent.state = new State(objectType);
////                    }
//
//                } else if (stateType == Type.request("list")) {
//
//                    // Change the types of the stored object if it is not a list
//                    if (featureState == null) {
//                        featureState = Structure.create(stateType);
//                    } else if (featureState.type != stateType) {
////                        featureContent.objectType = List.class;
////                        featureContent.object = new ArrayList<>();
//                        featureState = Structure.create(stateType);
//                    }
//
//                    // Update the object
//
//                } else if (stateType == Type.request("text")) {
//
//                    // Change the types of the stored object if it is not a string (for text)
////                    if (featureContent.state == null) {
////                        featureContent.state = new State(objectType);
////                    } else if (featureContent.state.types != objectType) {
//////                        featureContent.objectType = String.class;
//////                        featureContent.object = null;
////                        featureContent.state = new State(objectType);
////                    }
//
//
////                    // Encapsulate text state
////                    State state = State.getState(stateType);
////                    state.object = expression;
//
//                    // Encapsulate text state
//                    Structure construct = Structure.request(expression);
//
//
////                    if (Content.isText((String) object)) {
////                    if (feature.domain == null || feature.domain.contains((String) expression)) {
//                    if (feature.domain == null || feature.domain.contains(construct)) {
//                    // TODO: if (feature.domain == null || feature.domain.contains(state)) {
//                            List list = (List) featureState.object;
////                        contents.request(tag).state.object = (String) object;
////                            list.request(expression);
//                            list.add(construct);
//                        } else {
//                            System.out.println(Color.ANSI_RED + "Error: Specified " + stateType + " is not in the feature's domain." + Color.ANSI_RESET);
//                        }
////                    }
////                    } else {
////                        System.out.println("Error: Cannot assign non-text to text feature.");
////                    }
//
//                } else {
//
////                    // Change the types of the stored object if it is not a list
////                    if (state == null) {
////                        state = State.getPersistentState(contentType);
////                    } else if (state.types != contentType) {
//////                        featureContent.objectType = List.class;
//////                        featureContent.object = new ArrayList<>();
////                        state = State.getPersistentState(contentType);
////                    }
//
//                    // Encapsulate text state
//                    Structure construct = Structure.request(expression);
//
//                    // Add to the list in memory
////                    if (Content.isText((String) object)) {
//                    if (construct != null) {
////                        if (feature.domain == null || feature.domain.contains((String) expression)) { // TODO: Update domain to contain State objects so it can contain port and other Constructs
//                        if (feature.domain == null || feature.domain.contains(construct)) { // TODO: Update domain to contain State objects so it can contain port and other Constructs
//                            List list = (List) featureState.object;
////                        contents.request(tag).state.object = (String) object;
//                            list.add(construct);
//                        } else {
//                            System.out.println(Color.ANSI_RED + "Error: Specified " + stateType + " is not in the feature's domain." + Color.ANSI_RESET);
//                        }
//                    }
//
//                }
//            } else {
//                System.out.println(Color.ANSI_RED + "Error: Feature types mismatches object types." + Color.ANSI_RESET);
//            }
//
//
//            /*
//            if (contents.request(tag).types == Type.request("none")) {
//                // TODO: Can't assign anything to the feature object
//                System.out.println("Error: Cannot assign feature with types 'none'.");
//            } else if (contents.request(tag).types == Type.request("any")) {
//                // TODO: Verify that this is correct!
//                contents.request(tag).object = object;
//            } else if (contents.request(tag).types == Type.request("list")) {
//                List contentList = (List) contents.request(tag).object;
//
//                // TODO: Check types of list contents and restrict to the types (or any if "any")
//                // TODO: If specific text tokens are allowed AS WELL AS text construct, text construct subsumes the tokens and the tokens are not included in the domain
//
////                if (contents.request(address).listType == Type.request("text")) {
//                if (contents.request(tag).listTypes.contains(Type.request("text"))) {
//                    if (Content.isText((String) object)) {
//                        contentList.request(object);
//                    } else {
//                        System.out.println("Error: Cannot request non-text to list (only can contain text).");
//                    }
////                } else if (contents.request(address).listType == Type.request("construct")) {
////                } else if (contents.request(address).listTypes.contains(Type.request("construct"))) {
//                } else {
//                    // TODO: Determine if the construct object is allowed into the list based on the specific types!
//                    contentList.request(object);
//                }
//            } else if (contents.request(tag).types == Type.request("text")) {
//                if (Content.isText((String) object)) {
//                    contents.request(tag).object = (String) object;
//                } else {
//                    System.out.println("Error: Cannot assign non-text to text feature.");
//                }
//            } else {
//                contents.request(tag).object = object;
//            }
//            */
//        }
//    }

    @Override
    public String toString() {
        if (type == Type.request("text")) {
            String content = (String) this.object;
            return "'" + content + "' " + type + ".id=" + uid + "";
        } else if (type == Type.request("list")) {
            String content = "";
            List list = (List) this.object;
            for (int i = 0; i < list.size(); i++) {
                content += list.get(i);
                if ((i + 1) < list.size()) {
                    content += ", ";
                }
            }
            return type.identifier + ".id=" + uid + " : " + content;
        } else {
            return type.identifier + ".id=" + uid;
        }
    }

    public String toColorString() {
        if (type == Type.request("text")) {
            String content = (String) this.object;
            // return Color.ANSI_BLUE + type + Color.ANSI_RESET + " '" + content + "' (id: " + uid + ")" + " (uuid: " + uuid + ")";
            return  "'" + content + "' " + Color.ANSI_BLUE + type.identifier + Color.ANSI_RESET + ".id=" + uid;
        } else {
            return Color.ANSI_BLUE + type.identifier + Color.ANSI_RESET + ".id=" + uid;
            // return Color.ANSI_BLUE + type + Color.ANSI_RESET + " (id: " + uid + ")" + " (uuid: " + uuid + ")";
        }
    }
}
