import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product';
import { ProductService } from '../../services/product.service';
import { ProductListResponse } from '../../custom-responses/products/productlist.response';
import { environment } from '../../environment/env';
import { Category } from '../../models/category';
import { CategoryService } from '../../services/category.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  products: Product[] = [];

  // search
  keyword!: string;
  categories!: Category[];
  selectedCategoryId!: number;

  // pagination
  currentPage: number = 1; // NOTE: page param start at 0
  itemsPerPage: number = 12;
  totalProducts: number = 0;
  totalPages: number = 0;
  visiblePages: number[] = [];

  constructor(
    private _productService: ProductService,
    private _categoryService: CategoryService,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.getAllCategories();
    this.getAllProducts(
      (this.keyword = ''),
      (this.selectedCategoryId = 0),
      this.currentPage - 1,
      this.itemsPerPage
    );
  }

  searchProducts() {
    debugger;
    this.currentPage = 1;
    this.getAllProducts(
      this.keyword,
      this.selectedCategoryId,
      this.currentPage - 1,
      this.itemsPerPage
    );
  }

  // fetch categories
  getAllCategories() {
    this._categoryService.getAllCategories().subscribe({
      next: (response: Category[]) => {
        this.categories = response;
      },
      error: (error) => {
        console.log(error);
      },
      complete: () => {
        console.log('Categories loaded');
      },
    });
  }

  onCategoryChange(event: any) {
    this.selectedCategoryId = Number(event);
  }

  // fetch products
  getAllProducts(
    keyword: string = '',
    categoryId: number = 0,
    page: number,
    limit: number
  ) {
    this._productService
      .getProducts(keyword, categoryId, page, limit)
      .subscribe({
        next: (response: ProductListResponse) => {
          debugger;
          console.log(response);
          response.products.forEach((product) => {
            product.thumbnail_url = `${
              environment.apiBaseUrl
            }/products/images/${product.thumbnail_url || 'not-found.jpg'}`;
          });
          this.products = response.products;
          this.totalPages = response.total_pages;
          // set visible page
          this.visiblePages = this.getVisiblePage();
        },
        error: (error) => {
          debugger;
          console.log(error);
        },
        complete: () => {
          console.log('done');
        },
      });
  }

  onProductClick(productId: number) {
    debugger;
    this._router.navigate(['/products', productId]);
  }

  // PAGINATION
  onPageChange(page: number) {
    this.currentPage = page;
    this.getAllProducts(
      this.keyword,
      this.selectedCategoryId,
      this.currentPage - 1,
      this.itemsPerPage
    );
  }

  getVisiblePage() {
    const visiblePageCount = 5;
    const middleIndex = Math.floor(visiblePageCount / 2);
    let startIndex = Math.max(1, this.currentPage - middleIndex);
    let endIndex = Math.min(this.totalPages, startIndex + visiblePageCount - 1);
    if (endIndex - startIndex + 1 < visiblePageCount) {
      startIndex = Math.max(1, endIndex - visiblePageCount + 1);
    }
    return Array.from(
      { length: endIndex - startIndex + 1 },
      (_, i) => i + startIndex
    );
  }
}
