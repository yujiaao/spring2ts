
import {Address, GenericNestedObject, GenericChildObject, HelloResponse, GenericObject, Person, CollectionsObject} from "./model/model";
import {HelloController} from "./service/HelloController";

(window as any).APP_URL = "http://localhost:65380";

(window as any).HelloController = HelloController;

