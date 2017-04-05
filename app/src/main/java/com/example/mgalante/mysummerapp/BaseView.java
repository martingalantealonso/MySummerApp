/*
 *   ~           _MMMMM`
 *   ~     __MMMMMMMMM`       J        openTrends Solucions i Sistemes, S.L.
 *   ~ JMMMMMMMMMMMMF       JM         http://www.opentrends.net
 *   ~ MMMMMMMMMMF       _JMM`         info@opentrends.net
 *   ~ MMMMMMMF`    .JMMMMF`
 *   ~ """")    _JMMMMMMF`             Veneçuela, 105 1-A
 *   ~ _MMMMMMMMMMMMMMM`      .M)      Barcelona, 08019
 *   ~ MMMMMMMMMMMMMF`     .JMM`       Spain
 *   ~ MMMMMMMMMM"     _MMMMMF
 *   ~ M4MMM""`   ._MMMMMMMM`          *************************************
 *   ~ _______MMMMMMMMMMMF             BP002-Bonpreu
 *   ~ MMMMMMMMMMMMMMMM"               *************************************
 *   ~ MMMMMMMMMMMMF"                  Copyright (C) 2015 openTrends, Tots els drets reservats
 *   ~ MMMMMMMM""                      Copyright (C) 2015 openTrends, All Rights Reserved
 *   ~
 *   ~                                 This program is free software; you can redistribute it and/or modify
 *   ~                                 it under the terms of the GNU General Public License as published by
 *   ~                                 the Free Software Foundation; either version 2 of the License, or
 *   ~                                 (at your option) any later version.
 *   ~
 *   ~                                 This program is distributed in the hope that it will be useful,
 *   ~                                 but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   ~                                 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   ~                                 GNU General Public License for more details.
 *   ~
 *   ~                                 You should have received a copy of the GNU General Public License along
 *   ~                                 with this program; if not, write to the Free Software Foundation, Inc.,
 *   ~                                 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.example.mgalante.mysummerapp;


public interface BaseView<T> {
    void setPresenter(T presenter);
    void showMessage(String message);
}
