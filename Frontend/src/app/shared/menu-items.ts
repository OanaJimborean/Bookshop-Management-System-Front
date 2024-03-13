import { Injectable } from "@angular/core";
import { InjectableCompiler } from "@angular/compiler/src/injectable_compiler";

export interface Menu{
    state:string;
    name:string;
    type:string;
    icon:string;
    role:string;
}

const MENUITEMS = [
    {state:'dashboard', name:'Dashboard', type:'link', icon:'dashboard', role:''},
    {state:'category', name:'Manage Category', type:'link', icon:'category', role:'admin'},
    {state:'book', name:'Manage Book', type:'link', icon:'inventory_2', role:'admin'},
    {state:'order', name:'Manage Order', type:'link', icon:'shopping_cart', role:''},
    {state:'user', name:'Manage User', type:'link', icon:'people', role:'admin'},
    {state:'bill', name:'Manage Bill', type:'link', icon:'backup_table', role:''}
    
]

@Injectable()
export class MenuItems{
    getMenuitem():Menu[]{
        return MENUITEMS;
    }
}
