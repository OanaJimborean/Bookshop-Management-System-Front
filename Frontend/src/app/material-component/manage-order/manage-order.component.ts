import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { saveAs } from 'file-saver';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { BillService } from 'src/app/services/bill.service';
import { BookService } from 'src/app/services/book.service';
import { CategoryService } from 'src/app/services/category.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-manage-order',
  templateUrl: './manage-order.component.html',
  styleUrls: ['./manage-order.component.scss']
})
export class ManageOrderComponent implements OnInit {


  displayedColumns: string[] = ['title', 'category', 'price', 'quantity', 'total', 'edit'];
  dataSource: any = [];
  manageOrderForm:any = FormGroup;
  categories: any = [];
  books: any = [];
  price: any;
  totalAmount: number = 0;
  responseMessage: any;

  constructor(private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private bookService: BookService,
    private snackbarService: SnackbarService,
    private billService: BillService,
    private ngxService: NgxUiLoaderService
    ) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.getCategories();
    this.manageOrderForm = this.formBuilder.group({
      username: [null, [Validators.required, Validators.pattern(GlobalConstants.nameRegex)]],
      email: [null, [Validators.required, Validators.pattern(GlobalConstants.emailRegex)]],
      paymentMethod: [null, [Validators.required]],
      book: [null, [Validators.required]],
      category: [null, [Validators.required]],
      quantity: [null, [Validators.required]],
      price: [null, [Validators.required]],
      total: [0, [Validators.required]]
    });
    //this.tableData();
  }

  getCategories() {
    this.categoryService.getFilteredCategories().subscribe((response:any) => {
      this.ngxService.stop();
      this.categories = response;
    }, (error:any) => {
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      }
      else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  getBooksByCategory(value:any) {
    this.bookService.getBookByCategory(value.id).subscribe((response:any) => {
      this.books = response;
      this.manageOrderForm.controls['price'].setValue('');
      this.manageOrderForm.controls['quantity'].setValue('');
      this.manageOrderForm.controls['total'].setValue(0);
    }, (error:any) => {
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      }
      else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  getBookDetails(value:any) {
    this.bookService.getById(value.id).subscribe((response: any) => {
      this.price = response.price;
      this.manageOrderForm.controls['price'].setValue(response.price);
      this.manageOrderForm.controls['quantity'].setValue('1');
      this.manageOrderForm.controls['total'].setValue(this.price * 1);
    }, (error:any) => {
      this.ngxService.stop();
      console.log(error);
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      }
      else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  setQuantity(value:any) {
    var temp = this.manageOrderForm.controls['quantity'].value;
    if (temp > 0) {
      this.manageOrderForm.controls['total'].setValue(this.manageOrderForm.controls['quantity'].value * this.manageOrderForm.controls['price'].value);

    }
    else if(temp !=''){
      this.manageOrderForm.controls['quantity'].setValue('1');
      this.manageOrderForm.controls['total'].setValue(this.manageOrderForm.controls['quantity'].value * 
        this.manageOrderForm.controls['price'].value);
    }
  }

  validateBookAdd(){
    if(this.manageOrderForm.controls['total'].value === 0 || this.manageOrderForm.controls['total'].value === null || 
       this.manageOrderForm.controls['quantity'].value <= 0){
        return true;
       }
    else 
       return false;
  }

  validateSubmit(){
    if(this.totalAmount === 0 || this.manageOrderForm.controls['username'].value === null ||
    this.manageOrderForm.controls['email'].value === null || 
    this.manageOrderForm.controls['paymentMethod'].value === null){
      return true;
    }
    else
      return false;
  }

  add(){
    var formData = this.manageOrderForm.value;
    var bookName = this.dataSource.find((e:{id:number}) => e.id === formData.book.id);
    if(bookName === undefined){
      this.totalAmount = this.totalAmount + formData.total;
      console.log(formData);
      this.dataSource.push({ 
        id:formData.book.id,
        title:formData.book.title,
        category:formData.category.name,
        quantity:formData.quantity,
        price:formData.price, 
        total:formData.total
      });
      this.dataSource = [...this.dataSource];
      this.snackbarService.openSnackBar(GlobalConstants.bookAdded,"success");

    }else{
      this.snackbarService.openSnackBar(GlobalConstants.bookExistError,GlobalConstants.error);
    }
  }

  handleDeleteAction(value:any,element:any){
      this.totalAmount = this.totalAmount - element.total;
      this.dataSource.splice(value,1);
      this.dataSource = [...this.dataSource];
    }

    submitAction(){
      var formData = this.manageOrderForm.value;
      var data = {
        username: formData.username,
        email: formData.email,
        paymentMethod: formData.paymentMethod,
        totalAmount: this.totalAmount.toString(),
        bookDetails: JSON.stringify(this.dataSource)
      }

      this.ngxService.start();
      this.billService.generateReport(data).subscribe((respone:any)=>{
        this.downloadFile(respone?.uuid);
        this.manageOrderForm.reset();
        this.dataSource = [];
        this.totalAmount = 0;
      }, (error: any) => {
        this.ngxService.stop();
        console.log(error);
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        }
        else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      })
    }

    downloadFile(fileName:string){
      var data = {
        uuid:fileName
      }

      this.billService.getPdf(data).subscribe((response:any)=>{
        saveAs(response,fileName + '.pdf');
        this.ngxService.stop();
      })
    }


}
