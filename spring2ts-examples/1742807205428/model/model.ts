/**
 * @author S?awomir Dadas
 */
export interface Address {
    
    city: string;
    street: string;
    number: number;
    postalCode: string;
}

/**
 * @author S?awomir Dadas
 */
export interface GenericChildObject<T> extends GenericObject<T, number> {
    
}

/**
 * @author S?awomir Dadas
 */
export interface GenericNestedObject<T> {
    
    field: T;
}

/**
 * @author S?awomir Dadas
 */
export interface CollectionsObject {
    
    array: string[];
    list1: string[];
    list2: string[];
    list3: string[];
    vector: string[];
    set1: string[];
    set2: string[];
    set3: string[];
    set4: string[];
    set5: string[];
    set6: string[];
    map1: any;
    map2: any;
    map3: any;
    map4: any;
    map5: any;
    map6: any;
    queue1: string[];
    queue2: string[];
    queue3: string[];
}

/**
 * @author S?awomir Dadas
 */
export interface HelloResponse {
    
    id: number;
    greeting: string;
}

/**
 * @author S?awomir Dadas
 */
export interface GenericObject<T, E> {
    
    field: T;
    list: T[];
    nested: GenericNestedObject<GenericNestedObject<T>>[];
}

/**
 * @author S?awomir Dadas
 */
export interface Person {
    
    name: string;
    age: number;
    height: number;
    extraField1: any;
    extraField2: any;
    address: Address;
    dateOfBirth: number;
}

