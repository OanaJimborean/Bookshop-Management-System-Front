import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BookService {

  url = environment.apiUrl;

  constructor(private httpClient:HttpClient) { }

  add(data:any){
    return this.httpClient.post(this.url + 
      "/book/add", data,{
        headers: new HttpHeaders().set('Content-Type', "application/json")
      })
  }

  update(data:any){
    return this.httpClient.post(this.url + 
      "/book/update", data,{
        headers: new HttpHeaders().set('Content-Type', "application/json")
      })
  }

  getBooks(){
    return this.httpClient.get(this.url+"/book/get");
  }

  updateStatus(data:any){
    return this.httpClient.post(this.url + 
      "/book/updateStatus", data,{
        headers: new HttpHeaders().set('Content-Type', "application/json")
      })
  }

  delete(id:any){
    return this.httpClient.post(this.url + 
      "/book/delete/"+id,{
        headers: new HttpHeaders().set('Content-Type', "application/json")
      })
  }

  getBookByCategory(id:any){
    return this.httpClient.get(this.url+"/book/getByCategory/"+id);
  }

  getById(id:any){
    return this.httpClient.get(this.url+"/book/getById/"+id);
  }
}
