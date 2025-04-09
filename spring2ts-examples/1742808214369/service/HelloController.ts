
import {HelloResponse} from "../model/model";
import {prefix} from "../constant";
import {postJson, get} from "@/utils/request";



const cp: string =  prefix + '';

/** 
* @author S?awomir Dadas
*/
export const HelloController =  {
    
    
    /** 
    * Construct a new HelloController
    * @param name The name of the controller
    * @return  The created controller
    * ,
    */
    hello: async function (name: string): Promise<API.RequestResult<HelloResponse>> {
            const url = cp + '/hello';
            return get(url, {name});
    },

    /** 
    * ,
    */
    helloWithoutAtt: async function (name: string): Promise<API.RequestResult<HelloResponse>> {
            const url = cp + '/helloWithoutAtt';
            return get(url, {name});
    },

    /** 
    * ,
    * @param his InfoCarRelations 数据对象,required:true
    */
    helloJson1: async function (his: any[]): Promise<API.RequestResult<HelloResponse>> {
            const url = cp + '/hello-json1';
            return postJson(url, [...his]);
    },

    /** 
    * ,
    * @param his InfoCarRelations 数据对象,required:true
    */
    helloJson2: async function (his: any): Promise<API.RequestResult<HelloResponse>> {
            const url = cp + '/hello-json2';
            return postJson(url, {...his});
    },

}

