package com.example.clothessuggester.model

import com.example.clothessuggester.util.extensions.getRandomExcept

class ClothesDataBase {

    private val normalClothes = listOf(
        "https://www.fjallraven.com/496217/globalassets/catalogs/fjallraven/f8/f813/f81353/f356-021/skog_shirt_m_81353-356-021_a_main_fjr.jpg?width=2000&height=2000&mode=BoxPad&bgcolor=fff&quality=80",
        "https://cdn.shopify.com/s/files/1/0896/8970/products/redprint_2_2f59a503-3ef7-4991-a247-3d40e9e58e63.jpg?v=1668774891",
        "https://rukminim1.flixcart.com/image/832/832/xif0q/shirt/p/s/n/39-5135-ca-french-crown-original-imagh2y3fgygwkgv.jpeg?q=70"
    )
    private val hotClothes = listOf(
        "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.massimodutti.com%2Fww%2Fshort-sleeve-cotton-tshirt-l06823540&psig=AOvVaw13PI1iuLOjOgZ1REJ9Y9OK&ust=1681507816045000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCKDNvPPmp_4CFQAAAAAdAAAAABAE",
        "https://agnesb-agnesb-com-storage.omn.proximis.com/Imagestorage/imagesSynchro/0/0/8549751afbff06c38dcae58ee01a808bd850ca3c_2653J000_010_1.jpeg",
        "https://fabrilife.com/image-gallery/638741f4b738e-square.jpg"
    )
    private val coldClothes = listOf(
        "https://euro.montbell.com/products/prod_img/zoom/z_2301368_bric.jpg",
        "https://down-ph.img.susercontent.com/file/b12509df5340bbd6bcfffa2ad48f3110",
        "https://cdn.shopify.com/s/files/1/0017/2100/8243/products/LRX-4_BLACK_2000x.jpg?v=1675198623"
    )


    fun getColdClothes(except: String): String {
         return coldClothes.getRandomExcept(except)
    }

    fun getNormalClothes(except: String): String {
        return normalClothes.getRandomExcept(except)
    }

    fun getHotClothes(except: String): String {
        return hotClothes.getRandomExcept(except)
    }

}