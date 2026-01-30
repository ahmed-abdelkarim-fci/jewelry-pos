import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id: string;
  barcode: string;
  modelName: string;
  purityEnum: string;
  type: string;
  grossWeight: number;
  makingCharge: number;
  description?: string;
  status: string;
  costPrice: number;
  estimatedPrice: number;
  createdDate: string;
}

export interface ProductRequest {
  barcode: string;
  modelName: string;
  purityEnum: string;
  type: string;
  grossWeight: number;
  makingCharge: number;
  description?: string;
  costPrice: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/products';

  getAllProducts(page: number = 0, size: number = 20): Observable<PageResponse<Product>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'modelName');
    return this.http.get<PageResponse<Product>>(this.API_URL, { params });
  }

  getProductById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/${id}`);
  }

  getProductByBarcode(barcode: string): Observable<Product> {
    return this.http.get<Product>(`${this.API_URL}/barcode/${barcode}`);
  }

  searchProducts(query: string): Observable<Product[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Product[]>(`${this.API_URL}/search`, { params });
  }

  searchProductsAdvanced(
    query?: string,
    purity?: string,
    type?: string,
    minWeight?: number,
    maxWeight?: number
  ): Observable<Product[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    if (purity) params = params.set('purity', purity);
    if (type) params = params.set('type', type);
    if (minWeight !== undefined) params = params.set('minWeight', minWeight.toString());
    if (maxWeight !== undefined) params = params.set('maxWeight', maxWeight.toString());
    return this.http.get<Product[]>(`${this.API_URL}/search/advanced`, { params });
  }

  createProduct(product: ProductRequest): Observable<void> {
    return this.http.post<void>(this.API_URL, product);
  }

  updateProduct(id: string, product: ProductRequest): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/${id}`, product);
  }

  deleteProduct(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
