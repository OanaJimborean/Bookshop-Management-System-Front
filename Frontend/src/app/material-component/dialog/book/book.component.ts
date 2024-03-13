import { Component, EventEmitter, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BookService } from 'src/app/services/book.service';
import { CategoryService } from 'src/app/services/category.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-book',
  templateUrl: './book.component.html',
  styleUrls: ['./book.component.scss']
})
export class BookComponent implements OnInit {

  onAddBook = new EventEmitter();
  onEditBook = new EventEmitter();
  bookForm:any = FormGroup;
  dialogAction:any = "Add";
  action:any = "Add";
  categories:any = [];

  responseMessage:any;

  constructor(@Inject(MAT_DIALOG_DATA) public dialogData:any,
  private formBuilder:FormBuilder,
  private bookService:BookService,
  public dialogRef: MatDialogRef<BookComponent>,
  private categoryService:CategoryService,
  private snackbarService:SnackbarService,) { }

  ngOnInit(): void {
    this.bookForm = this.formBuilder.group({
      title:[null, [Validators.required]],
      author:[null, [Validators.required]],
      categoryId:[null, [Validators.required]],
      price:[null, [Validators.required]],
      description:[null, [Validators.required]]
    });
    if(this.dialogData.action === 'Edit'){
    this.dialogAction = "Edit";
    this.action = "Update"
    this.bookForm.patchValue(this.dialogData.data);
    }
    this.getCategories();
  }

  getCategories(){
    this.categoryService.getCategories().subscribe((response:any)=>{
      this.categories = response;
    },(error:any)=>{
      console.log(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      }
      else{
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage,GlobalConstants.error);
    })
  }

  handleSubmit(){
    if(this.dialogAction === "Edit"){
      this.edit();
    }
    else{
      this.add();
    } 
  }

  add(){
    var formData = this.bookForm.value;
    var data = {
      title: formData.title,
      author: formData.author,
      categoryId:formData.categoryId,
      description:formData.description,
      price:formData.price
      
    }

    this.bookService.add(data).subscribe((response:any)=>{
      this.dialogRef.close();
      this.onAddBook.emit();
      this.responseMessage = response.message;
      this.snackbarService.openSnackBar(this.responseMessage, "success");
    },(error)=>{
      console.log(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      }
      else{
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    });
  }

  edit(){
    var formData = this.bookForm.value;
    var data = {
      id:this.dialogData.data.id,
      title: formData.title,
      author: formData.author,
      categoryId:formData.categoryId,
      description:formData.description,
      price:formData.price
      
    }

    this.bookService.update(data).subscribe((response:any)=>{
      this.dialogRef.close();
      this.onEditBook.emit();
      this.responseMessage = response.message;
      this.snackbarService.openSnackBar(this.responseMessage, "success");
    },(error)=>{
      this.dialogRef.close();
      console.error(error);
      if(error.error?.message){
        this.responseMessage = error.error?.message;
      }
      else{
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    });
  }


}
